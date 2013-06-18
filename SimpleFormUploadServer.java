//package simpleformupload;


import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerFileUpload;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.platform.Verticle;


/**
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
public class SimpleFormUploadServer extends Verticle {
  public void start() {
    System.out.println("started");
		println("started");
    vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
      public void handle(final HttpServerRequest req) {
        if (req.uri().equals("/")) {
          // Serve the index page
          req.response().sendFile("simpleformupload/index.html");
        } else if (req.uri().startsWith("/form")) {
          req.uploadHandler(new Handler<HttpServerFileUpload>() {
            @Override
            public void handle(final HttpServerFileUpload upload) {
              upload.exceptionHandler(new Handler<Throwable>() {
                @Override
                public void handle(Throwable event) {
                  req.response().end("Upload failed");
                }
              });
              upload.endHandler(new Handler<Void>() {
                @Override
                public void handle(Void event) {
                  req.response().end("Upload successful, you should see the file in the server directory");
                }
              });
              upload.streamToFileSystem(upload.filename());
            }
          });
        } else {
          req.response().setStatusCode(404);
          req.response().end();
        }
      }
    }).listen(8081);
  }
}
