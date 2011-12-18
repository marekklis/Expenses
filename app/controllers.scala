package controllers

import play._
import play.mvc._
import models._
import java.text.SimpleDateFormat
import java.sql.Date
import java.util.Calendar

object Application extends Controller {

  import views.Application._

  def index = html.index(Repository.getAllExpanses, Repository.sum)

  def addExpense = html.editExpense(
    Expense(0, "", 0.0, new Date(Calendar.getInstance().getTimeInMillis())), "Add new expense")

  def editExpense(id: Int) = {
    val expense = Repository.findExpenseById(id)
    html.editExpense(expense, "Edit expense " + expense.name)
  }

  def saveExpense = {
    val id = params.get("id").toInt
    val name = params.get("name")
    val price = params.get("price").toDouble
    val dateString = params.get("date")
    val format = new java.text.SimpleDateFormat("yyyy-MM-dd")
    var date = new java.util.Date()
    if (dateString != null) {
      date = format.parse(dateString)
    }
    Repository.saveExpense(Expense(id, name, price, new Date(date.getTime())))
    Action(index)
  }

}
