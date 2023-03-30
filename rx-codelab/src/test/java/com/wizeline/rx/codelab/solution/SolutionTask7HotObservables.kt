package com.wizeline.rx.codelab.solution

import com.wizeline.rx.codelab.RxLogging
import com.wizeline.rx.codelab.http.HttpModule
import com.wizeline.rx.codelab.http.RequestInfo
import com.wizeline.rx.codelab.server.RxServerFactory
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class SolutionTask7HotObservables {

  private val rxServer = RxServerFactory.create()
  private val httpBinApi = HttpModule.httpBinApi()

  @Before
  fun before() {
    RxLogging.enableObservableSubscribeLogging()
  }

  @Test
  fun coldObservable() {
    val getRequest = httpBinApi.getRequest()
      .subscribeOn(Schedulers.io())
      .share()

    getRequest.subscribe { println(it) }
    getRequest.subscribe { println(it) }
  }

  @Test
  fun hotObservable() {
    val logs = rxServer.debugLogsHot()

    logs.delaySubscription(250, TimeUnit.MILLISECONDS).subscribe { println(it) }
    logs.subscribe { println(it) }
  }

  @Test
  fun createHotObservableThroughSubject() {
    val publishProcessor = PublishProcessor.create<RequestInfo>()

    publishProcessor.subscribe { println(it) }
    publishProcessor.subscribe { println(it) }

    val request = httpBinApi.getRequest().subscribeOn(Schedulers.io())

    request.subscribe(publishProcessor::onNext, publishProcessor::onError)
  }

  @After
  fun after() {
    Thread.sleep(500)
    HttpModule.awaitNetworkRequests()
  }
}
