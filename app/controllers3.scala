package controllers

import play._
import play.mvc._
import play.mvc.Http.Request
import play.mvc.results.Status
import play.mvc.results.Result
import model.SampleRequest
import daos.files.TestFileDao

object Application3 extends Controller with controllers.stub.SampleStub {

    def start() = {
      startServer()
    }

    def stop() = {
      stopServer()
    }

}