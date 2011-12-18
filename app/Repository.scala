import org.scalaquery.session._
import org.scalaquery.session.Database.threadLocalSession
import org.scalaquery.ql.basic.BasicDriver.Implicit._
import org.scalaquery.ql.extended.{ExtendedTable => Table}
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql._

package models {

import java.sql.Date

case class Expense(id: Int, name: String, price: Double, date: Date)

object Expenses extends Table[Expense]("EXPENSES") {
  def id = column[Int]("ID", O PrimaryKey, O AutoInc)

  def name = column[String]("NAME")

  def price = column[Double]("PRICE")

  def date = column[Date]("WHEN")

  def * = id ~ name ~ price ~ date <>(Expense, Expense.unapply _)

  def noID = name ~ price ~ date

}


object Repository {

  val db = Database.forDataSource(play.db.DB.datasource)

  def sum = {
    var sumOfPrices = 0.0
    db withSession {
      val e = Query(Expenses)
      e.list foreach {
        ex => sumOfPrices += ex.price
      }
    }
    sumOfPrices
  }

  def getAllExpanses = {
    db withSession {
      Query(Expenses).list
    }
  }

  def saveExpense(expense: Expense) = {
    db withSession {
      if (expense.id == 0)
        Expenses.noID insert(expense.name, expense.price, expense.date)
      else {
        val q = (for (e <- Expenses if e.id === expense.id) yield e.name ~ e.price ~ e.date).
          update(expense.name, expense.price, expense.date)
      }
    }
  }

  def findExpenseById(id: Int) = {
    db withSession {
      Query(Expenses).where(_.id === id).first
    }
  }

}

}
