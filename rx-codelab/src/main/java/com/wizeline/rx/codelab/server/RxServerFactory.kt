package com.wizeline.rx.codelab.server

object RxServerFactory {
  fun create(): RxServer {
    return ChattyServer.create()
  }
}
