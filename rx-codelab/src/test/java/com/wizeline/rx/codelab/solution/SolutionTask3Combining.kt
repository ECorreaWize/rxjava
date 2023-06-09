package com.wizeline.rx.codelab.solution

import com.wizeline.rx.codelab.RxLogging
import com.wizeline.rx.codelab.combineLatest
import com.wizeline.rx.codelab.http.GitHubConverter
import com.wizeline.rx.codelab.http.HttpModule
import com.wizeline.rx.codelab.http.UserCache
import com.wizeline.rx.codelab.zipWith
import org.junit.Before
import org.junit.Test

class SolutionTask3Combining {
  private val gitHubApi = HttpModule.mockedGitHubApi()

  @Before
  fun setUp() {
    RxLogging.enableObservableSubscribeLogging()
  }

  @Test
  fun zipWith_userWithRepos() {
    gitHubApi.getUser(LOGIN)
      .zipWith(gitHubApi.getRepos(LOGIN), GitHubConverter::convert)
      .subscribe { println(it) }
  }

  @Test
  fun startWith_userInCache() {
    gitHubApi.getUser(LOGIN)
      .map { GitHubConverter.convert(it) }
      .startWith(UserCache.getUser(LOGIN))
      .subscribe { println(it) }
  }

  @Test
  fun merge_userInCache() {
    UserCache.getUser(LOGIN)
      .mergeWith(gitHubApi.getUser(LOGIN)
        .map { GitHubConverter.convert(it) })
      .subscribe { println(it) }
  }

  @Test
  fun combineLatest_cachedUserWithRepos() {
    val userObservable = gitHubApi.getUser(LOGIN)
      .map { GitHubConverter.convert(it) }
      .startWith(UserCache.getUser(LOGIN))

    val reposObservable = gitHubApi.getRepos(LOGIN)
      .map { GitHubConverter.convert(it) }


    reposObservable.combineLatest(userObservable, GitHubConverter::convert)
      .subscribe { println(it) }
  }

  companion object {
    private const val LOGIN = "defunkt"
  }
}
