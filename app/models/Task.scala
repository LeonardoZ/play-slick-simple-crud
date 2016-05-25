package models

import java.sql.Timestamp

case class Task(id: Option[Long],
                name: String,
                description: String,
                completed: Boolean = false,
                completedWhen: Option[Timestamp] = None) {
  def complete(when: Timestamp): Task = this.copy(completed = true, completedWhen = Some(when))
}
