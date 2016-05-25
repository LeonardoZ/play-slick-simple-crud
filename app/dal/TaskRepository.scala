package dal

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import models.Task

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class TaskRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
    // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class TaskTable(tag: Tag) extends Table[Task](tag, "tasks") {
    
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    
    def name = column[String]("name")
    
    def description = column[String]("description")

    def completed = column[Boolean]("completed")

    def completedWhen = column[Timestamp]("completed_when")

    def * = (id.?, name, description, completed, completedWhen.?) <> (Task.tupled, Task.unapply)
    
  } 
  
  private val tasks = TableQuery[TaskTable]
  
  def findById(id: Long): Future[Option[Task]] = db.run {
    tasks.filter{_.id === id}.result.headOption
  }

  def create(task: Task): Future[Int] =  db.run {
    tasks += task
  }

  def update(task: Task): Future[Int] = db.run {
    tasks.filter(_.id === task.id).update(task)
  }

  def delete(id: Long): Future[Int] = db.run {
    tasks.filter(_.id === id).delete
  }

  def list(): Future[Seq[Task]] = db.run {
    tasks.result
  }
}

