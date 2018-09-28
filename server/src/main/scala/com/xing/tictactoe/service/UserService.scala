package com.xing.tictactoe.service

import cats.effect.{ContextShift, IO}
import cats.effect.concurrent._
import io.circe.{Encoder, Json}
import io.circe.syntax._
import org.http4s.{HttpRoutes, Response}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import cats.syntax.all._
import UserTypes._

object UserTypes {
  type Sessions = Map[String, String]
  type Users = Map[String, User]
  type SessionsRef = Ref[IO, Sessions]
  type UsersRef = Ref[IO, Users]
}

case class User(name: String, password: String)

class UserService(usersRef: UsersRef, sessionsRef: SessionsRef)(
    implicit val cs: ContextShift[IO])
    extends Http4sDsl[IO] {

  implicit val UserEncoder: Encoder[User] =
    Encoder.instance { user: User =>
      Json.obj("name" -> Json.fromString(user.name))
    }

  object UsernameQueryParamMatcher
      extends QueryParamDecoderMatcher[String]("username")
  object PasswordQueryParamMatcher
      extends QueryParamDecoderMatcher[String]("password")

  val service: HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
      // Get a list of all users
      case GET -> Root / username =>
        val userResult =
          withReference(usersRef, getUserByUsername(username))

        userResult.flatMap {
          case Some(user) => Ok(user.asJson)
          case None       => NotFound("User not found")
        }

      case GET -> Root =>
        Ok(withReference(usersRef, getUsers()).map(_.asJson))

      case PUT -> Root :? UsernameQueryParamMatcher(username) +& PasswordQueryParamMatcher(
            password) =>
        val newUser =
          withReference(usersRef, createUser(username, password))

        newUser.flatMap {
          case Right(user) =>
            val username = user.name
            updateReference(usersRef, saveUser(user)) >> Ok(
              s"User $username created.")

          case Left(error) => BadRequest(error)
        }

      case DELETE -> Root / username =>
        updateReference(usersRef, deleteUser(username)) >> Ok(
          s"User $username deleted.")

      case POST -> Root / "authenticate" :? UsernameQueryParamMatcher(username) +& PasswordQueryParamMatcher(
            password) =>
        val authorizedUser =
          withReference(usersRef, authenticate(username, password))

        authorizedUser.flatMap {
          case Right(user) =>
            val token = generateToken()
            updateReference(sessionsRef, login(user, token)) >> Ok(
              Json.obj("user" -> user.asJson,
                       "token" -> Json.fromString(token)))
          case Left(error) => BadRequest(error)
        }
    }
  }

  def withReference[A, B](ref: Ref[IO, Map[String, A]],
                          f: Map[String, A] => B): IO[B] =
    for {
      map <- ref.get
    } yield f(map)

  def updateReference[A](ref: Ref[IO, Map[String, A]],
                         f: Map[String, A] => Map[String, A]): IO[Unit] =
    ref.update(f)

  def saveUser(user: User)(users: Users): Users = {
    users + (user.name -> user)
  }

  def login(user: User, token: String)(sessions: Sessions): Sessions = {
    sessions + (user.name -> token)
  }

  def deleteUser(name: String)(users: Users): Users = {
    users - name
  }

  def getUsers()(users: Users): Users = {
    users
  }

  def getUserByUsername(username: String)(users: Users): Option[User] = {
    users.get(username)
  }

  def userExists(username: String)(users: Users): Boolean = {
    val user = getUserByUsername(username)(users)

    user match {
      case Some(_) => true
      case None    => false
    }
  }

  def createUser(username: String, password: String)(
      users: Users): Either[String, User] = {

    if (userExists(username)(users)) return Left("User does already exist.")

    Right(User(username, password))
  }

  def generateToken(): String =
    scala.util.Random.alphanumeric.take(20).mkString("")

  def authenticate(username: String, password: String)(
      users: Users): Either[String, User] =
    getUserByUsername(username)(users) match {
      case Some(user) =>
        if (user.password == password) { return Right(user) }
        Left("Password is wrong")
      case None => Left("User does not exist.")
    }
}
