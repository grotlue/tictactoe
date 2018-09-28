package com.xing.tictactoe.service

import cats.effect.{ContextShift, IO}
import cats.effect.concurrent._
import io.circe.{Encoder, Json}
import io.circe.syntax._
import org.http4s.{HttpRoutes, Response}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import cats.syntax.all._

case class User(name: String, password: String)

class UserService(usersRef: Ref[IO, Map[String, User]])(
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
  type UserMap = Map[String, User]

  val service: HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
      // Get a list of all users
      case GET -> Root / username =>
        val user = withReference(usersRef, getUserByUsername(username))

        user.flatMap {
          case Some(us) => Ok(Json.obj("name" -> us.name.asJson))
          case None     => NotFound("User not found")
        }

      case GET -> Root =>
        Ok(withReference(usersRef, getUsers()).map(_.asJson))

      case PUT -> Root :? UsernameQueryParamMatcher(username) +& PasswordQueryParamMatcher(
            password) =>
        val newUser = withReference(usersRef, createUser(username, password))

        newUser.flatMap {
          case Right(user) =>
            val username = user.name
            updateReference(usersRef, saveUser(user)) >> Ok(
              s"User with $username was created")

          case Left(error) => BadRequest(error)
        }

//      case POST -> Root :? UsernameQueryParamMatcher(username) +& PasswordQueryParamMatcher(
//            password) =>
//        authenticate(usersRef, username, password) match {
//          case Right(user) =>
//            val username = user.name
//            Ok(s"Hey $username, you are logged in")
//          case Left(error) => BadRequest(error)
//        }
    }
  }

  def withReference[A](ref: Ref[IO, Map[String, User]],
                       f: Map[String, User] => A): IO[A] =
    for {
      map <- ref.get
    } yield f(map)

  def updateReference(ref: Ref[IO, Map[String, User]],
                      f: Map[String, User] => Map[String, User]): IO[Unit] =
    ref.update(f)

  def saveUser(user: User)(users: Map[String, User]): Map[String, User] = {
    users + (user.name -> user)
  }

  def getUsers()(users: Map[String, User]): Map[String, User] = {
    users
  }

  def getUserByUsername(username: String)(
      users: Map[String, User]): Option[User] = {
    users.get(username)
  }

  def userExists(username: String)(users: Map[String, User]): Boolean = {
    val user = getUserByUsername(username)(users)

    user match {
      case Some(_) => true
      case None    => false
    }
  }

  def createUser(username: String, password: String)(
      users: Map[String, User]): Either[String, User] = {

    if (userExists(username)(users)) return Left("User does already exist.")

    Right(User(username, password))
  }

  def authenticate(usersIO: Ref[IO, Map[String, User]],
                   username: String,
                   password: String): Either[String, User] =
    ???

}
