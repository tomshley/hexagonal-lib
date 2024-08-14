package com.tomshley.hexagonal.lib.staticassets

protected[staticassets] trait WebJars {
  /*
  UNDER CONSTRUCTION!
   */
  /*
    /**
   * Complete a get request from a webjar asset
   *
   * @param locator The webjar asset locator
   * @param webJar The webjar name
   * @param path The path of the asset within the JAR
   */
  def getFromWebjar(locator: WebJarAssetLocator, webJar: String, path: String): Route =
    Try(locator.getFullPathExact(webJar, path)) match {
      case Success(fullPath)                    => getFromResource(fullPath)
      case Failure(_: IllegalArgumentException) => reject
      case Failure(other)                       => failWith(other)
    }

  /**
   * Get a directory from a webjar.
   *
   * Special-cases "index.html" at the root: If the root directory is requested
   * serve index.html, if index.html is requested redirect permanently to the
   * root directory.  This creates nicer URLs.
   *
   * @param locator The asset locator to use
   * @param webJar The name of the webjar to serve
   */
  def getDirectoryFromWebjar(locator: WebJarAssetLocator, webJar: String): Route =
    concat(
      // If we are at the root directory add a trailing slash so that relative
      // paths to resources are correctly resolved and then serve index.html
      (pathEndOrSingleSlash & redirectToTrailingSlashIfMissing(MovedPermanently)) {
        getFromWebjar(locator, webJar, "index.html")
      },
      // If the path immediately ends in /index.html redirect to the directory, that is
      // the path already matched by the surrounding pathPrefix.
      pathPrefixTest("index.html" ~ PathEnd) {
        (extractUri & extractMatchedPath) { (uri, path) =>
          redirect(uri.withPath(path ++ Uri.Path.SingleSlash), MovedPermanently)
        }
      },
      // Otherwise if the path points to something below this directory serve the
      // asset at that path from the webjar.  Require that the path starts with a
      // slash to make sure that we only serve assets within the directory.
      (rawPathPrefix(Slash) & extractUnmatchedPath) { path =>
        getFromWebjar(locator, webJar, path.toString)
      }
    )
}
   */
  /*
  val webJarAssetLocator = new WebJarAssetLocator

  final def sbtWeb: Route = {
    pathPrefix("lib") {
      pathPrefix(Segment) {
        webJars
      }
    } ~ webJars
  }

  final def webJars(webJarName: String): Route = {
    extractUnmatchedPath { path =>
      Try(webJarAssetLocator.getFullPath(webJarName, path.toString)) match {
        case Success(fullPath) =>
          getFromResource(fullPath)
        case Failure(_: IllegalArgumentException) =>
          reject
        case Failure(e) =>
          failWith(e)
      }
    }
  }

  final def webJars: Route = {
    extractUnmatchedPath { path =>
      Try(webJarAssetLocator.getFullPath(path.toString)) match {
        case Success(fullPath) =>
          getFromResource(fullPath)
        case Failure(_: IllegalArgumentException) =>
          reject
        case Failure(e) =>
          failWith(e)
      }
    }
  }
 */
}
