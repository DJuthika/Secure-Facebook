/*
Class to create a facebook user depending on the number of users provided by Client.scala.
Facebookusers are created in FacebookUsers.scala.
*/
package facebookClient.clientService

import akka.actor.Actor
import common.StartClientRequests
import akka.io.IO
import scala.collection.convert.decorateAsScala.mapAsScalaConcurrentMapConverter
import spray.can.Http
import scala.concurrent.Future
import akka.util.Timeout
import akka.pattern.ask
import spray.http._
import HttpMethods._
import akka.actor.ActorSystem
import scala.concurrent.duration._
import common.SendRequestToServer
import akka.actor.Props
import scala.concurrent.duration
import akka.actor.ActorRef
import java.util.concurrent.ConcurrentHashMap
import common.RegisterUser
import java.security.PublicKey

class ClientService(numOfUsers : Int,getRequestRate : Int,postRequestRate : Int, actorsystem : ActorSystem) extends Actor {
  
  implicit val system: ActorSystem = actorsystem
  implicit val timeout: Timeout = Timeout(15.seconds)
  var publicKeyHashMap : collection.concurrent.Map[String,PublicKey] = new ConcurrentHashMap().asScala
  import system.dispatcher

  def scheduleRequest = {
    for ( i <- 0 to numOfUsers-1){
          var facebookUser = system.actorOf(Props(new FacebookUsers(numOfUsers,postRequestRate,publicKeyHashMap)), name="user" + i)
          facebookUser ! RegisterUser
    }

  }
  
  
  def receive = {  
    case StartClientRequests => println("Scheduling Requests")
     scheduleRequest
  }
}
