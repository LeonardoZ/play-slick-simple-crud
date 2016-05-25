package controllers

import java.sql.{JDBCType, Timestamp}

import play.api.libs.json.Json
import play.api.mvc._
import play.api.i18n._
import play.api.data.Form
import play.api.data.Forms._
import models._
import dal._

import scala.concurrent.{ExecutionContext, Future}
import javax.inject._


class TaskController @Inject()(repo: TaskRepository, val messagesApi: MessagesApi)
                              (implicit ec: ExecutionContext) extends Controller with I18nSupport {

  val taskForm: Form[TaskForm] = Form {
    mapping(
      "id" -> optional(longNumber),
      "name" -> nonEmptyText(minLength = 2, maxLength = 170),
      "description" -> nonEmptyText(minLength = 2, maxLength = 255)
    )(TaskForm.apply)(TaskForm.unapply)
  }


  def list = Action.async {
    repo.list().map { xs =>
      Ok(views.html.listTasks(xs))
    }
  }

  def addTaskForm = Action {
    Ok(views.html.taskForm(taskForm, None))
  }

  def edit(id: Long) = Action.async {
    repo.findById(id).map {
      case Some(task) => Ok(views.html.taskForm(toFilledForm(task), Some(id)))
      case None => Redirect(routes.TaskController.list())
    }
  }

  def update(id: Long) = Action.async { implicit request =>
    taskForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.taskForm(errorForm, Some(id))))
      },
      form => for (
        ent <- repo.findById(id).map(x => x.get);
        x <- repo.update(Task(ent.id, form.name, form.description))
      ) yield Redirect(routes.TaskController.list())
    )
  }

  def toFilledForm(task: Task): Form[TaskForm]
      = taskForm.fill(TaskForm(task.id, task.name, task.description))


  def delete(id: Long) = Action.async {
    repo.delete(id).map { _ => Ok }
  }

  def addTask = Action.async { implicit request =>
    taskForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.taskForm(errorForm, None)))
      },
      taskF => {
        repo.create(Task(None, taskF.name, taskF.description))
          .map {
            _ => Redirect(routes.TaskController.list)
          }
      }
    )
  }

  val completeForm = Form(tuple(
    "id" -> longNumber,
    "when" -> nonEmptyText
  ))

  def completeTask = Action.async( implicit request =>
    completeForm.bindFromRequest().fold(
      errorForm => {
        Future.successful(BadRequest(errorForm.errors.toString()))
      },
      form => {
        for (
          opt <- repo.findById(form._1).map(x => x.get);
          res <- repo.update(opt.complete(timestampFormat.readsValue(form._2)));
          task <- repo.findById(form._1).map(x => x.get)
        ) yield Ok(Json.toJson(task))
      }
    )

  )
}

case class TaskForm(id: Option[Long], name: String, description: String)