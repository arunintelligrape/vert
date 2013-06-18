package webapp

import org.vertx.groovy.core.http.RouteMatcher
import static org.vertx.groovy.core.streams.Pump.createPump

import org.vertx.java.core.Handler;
//import org.vertx.java.core.http.HttpServerFileUpload;
import org.vertx.java.core.http.HttpServerRequest;
//import org.vertx.java.platform.Verticle;

def log = container.logger

def routeMatcher = new RouteMatcher()
int cnt=0;


routeMatcher.get("/index") { req ->
    println "#----index.html----"
    req.response.sendFile "web/index.html"
    println "#### request url is :"+req.uri
}




routeMatcher.noMatch{ req ->
    println "#### request url is (nomatch) : web"+req.uri

//    req.response.end "Nothing matched"+req.uri
    req.response.sendFile "web" + req.path
}


routeMatcher.all("/testcopyfile"){ req ->
        println ("## copy -- 1")

     req.uploadHandler { upload,xx ->
        upload.exceptionHandler { cause ->
        req.response.end("Upload failed");
      }

      upload.endHandler {
        req.response.end("Upload successful, you should see the file in the server directory");
      }
      println ("## copy -- 4")
      upload.streamToFileSystem(upload.filename);
    }
}

routeMatcher.all("/rrcopyfile"){ req ->
        cnt++;
	def filename = "upload.txt"
	def fs = vertx.fileSystem
	println "##--------1"
	fs.props(filename) { ares ->
	 println "##-------2"
// 	 def props = ares.result
 //	 println "props is ${props}"
 //	 def size = props.size
//	 println "##-------3"
 //	 req.headers["content-length"] = size
 	fs.open(filename) { ares2 ->
	 println "##-------4"
   	 def file = ares2.result
   	 def rs = file.readStream
   	 def pump = createPump(rs, req)
         println "##-------5"
   	 rs.endHandler { req.end() }
  	  pump.start()
  		}
	}
}


routeMatcher.all("/copyfile"){ req ->
	cnt++;
	println "#### request url is :"+req.uri
	req.pause()
//	  def filename = "${UUID.randomUUID()}.uploaded"
          def filename = "upload_img/ccd"+cnt+""
	  vertx.fileSystem.open(filename) { ares ->
            def file = ares.result
	    def pump = createPump(req, file.writeStream)
  	     req.endHandler {
  	    file.close {
      	 	 println "Uploaded ${pump.bytesPumped} bytes to $filename"
       		 req.response.end()
 	       }
   	    }
 	   pump.start()
 	   req.resume()
	   println ("## copy file is:"+file)
 	 }


//    println "#### request url is :"+req.uri
//    req.response.sendFile   "SimpleFormUploadServer.java"
//    String output ="";
//    cnt++;
//    vertx.fileSystem.copy("a.txt", "b_"+cnt+".txt") { ar ->
//        if (ar.succeeded()) {
//		println "## 11"
  //          output="Copied successfully";
  //      }
  //      else {
//		println "## 22"
 //           output = "Fail to copy"+ ar.exception;
 //       }
 //       req.response.end "####"+output;
 //   }
}





vertx.createHttpServer().requestHandler(routeMatcher.asClosure()).listen(8080, "localhost")

log.info "Server running at http://localhost:8080/"


