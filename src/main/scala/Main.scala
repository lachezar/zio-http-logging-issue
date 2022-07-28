package main

import zhttp.http.*
import zhttp.http.middleware.HttpMiddleware
import zhttp.http.middleware.Web.PartialInterceptZIOPatch
import zhttp.service.{EventLoopGroup, Server}
import zhttp.service.server.ServerChannelFactory
import zio.*
import zio.logging.backend.SLF4J

object Main extends ZIOAppDefault {

  private val loggingLayer: ZLayer[Any, Nothing, Unit] =
    Runtime.removeDefaultLoggers ++ SLF4J.slf4j(LogLevel.Debug)

  val logRequestInfo: HttpMiddleware[Any, Nothing] =
    PartialInterceptZIOPatch(req =>
      ZIO
        .clockWith(_.nanoTime)
        .map(start => (req.method, req.url, start, req.bodyAsString))
    ) { case (response, (method, url, start, reqBodyTask)) =>
      for {
        end <- ZIO.clockWith(_.nanoTime)
        // the logging here does not use the configured SLF4J logger, but instead it uses the default logger setup
        _ <- ZIO
          .logInfo(
            s"${response.status.asJava.code()} $method ${url.encode} ${(end - start) / 1000000}ms"
          )
        // .provideSomeLayer(loggingLayer) <- if we explicitly provide the logging layer, then it uses it correctly
      } yield Patch.empty
    }

  val httpApp: HttpApp[Any, Nothing] = Http.collectZIO[Request] {
    case Method.GET -> !! / "ping" => ZIO.succeed(Response.text("pong!"))
  } @@ logRequestInfo

  val server = Server.port(8000) ++ Server.app(httpApp)
  val run = server.make
    .flatMap(start =>
      // This is correctly logged using the SLF4J setup
      ZIO.logWarning(s"Server started on port ${start.port}") *> ZIO.never
    )
    .provideSomeLayer(
      ServerChannelFactory.auto ++ EventLoopGroup.auto(0) ++ loggingLayer
    )
}
