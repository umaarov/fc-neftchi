package uz.umarov.fcneftchi.ui.news

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.domain.usecase.GetNewsDetailUseCase
import uz.umarov.fcneftchi.domain.usecase.IsArticleBookmarkedUseCase
import uz.umarov.fcneftchi.domain.usecase.ToggleArticleBookmarkUseCase
import uz.umarov.fcneftchi.util.MainDispatcherRule

@OptIn(ExperimentalCoroutinesApi::class)
class NewsArticleViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val url = "https://example.com/a"

    private val article = NewsArticle(
        id = "42",
        title = "Neftchi win",
        imageUrl = "img",
        date = "10 April 2026",
        content = "body",
        description = "desc",
        category = "cat",
        url = url,
    )

    private val getNewsDetail = mockk<GetNewsDetailUseCase>()
    private val isArticleBookmarked = mockk<IsArticleBookmarkedUseCase>()
    private val toggleArticleBookmark = mockk<ToggleArticleBookmarkUseCase>(relaxed = true)

    private fun viewModel(): NewsArticleViewModel = NewsArticleViewModel(
        getNewsDetail = getNewsDetail,
        isArticleBookmarked = isArticleBookmarked,
        toggleArticleBookmark = toggleArticleBookmark,
        savedStateHandle = SavedStateHandle(mapOf("articleUrl" to url)),
    )

    @Test
    fun `emits Loading then Success when article resolves`() = runTest {
        every { getNewsDetail(url) } returns flow { emit(article) }
        every { isArticleBookmarked(url) } returns MutableStateFlow(false)

        val vm = viewModel()

        vm.uiState.test {
            // initial value from stateIn before any upstream has run
            val initial = awaitItem()
            assertThat(initial.isLoading).isTrue()
            assertThat(initial.article).isNull()

            advanceUntilIdle()

            val resolved = awaitItem()
            assertThat(resolved.isLoading).isFalse()
            assertThat(resolved.article).isEqualTo(article)
            assertThat(resolved.isBookmarked).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits error state when detail flow throws`() = runTest {
        every { getNewsDetail(url) } returns flow { throw IllegalStateException("boom") }
        every { isArticleBookmarked(url) } returns MutableStateFlow(false)

        val vm = viewModel()

        vm.uiState.test {
            skipItems(1) // initial Loading
            advanceUntilIdle()

            val errorState = awaitItem()
            assertThat(errorState.isLoading).isFalse()
            assertThat(errorState.error).isEqualTo("boom")
            assertThat(errorState.article).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `reflects bookmark state changes from repository flow`() = runTest {
        val bookmarked = MutableStateFlow(false)
        every { getNewsDetail(url) } returns flow { emit(article) }
        every { isArticleBookmarked(url) } returns bookmarked

        val vm = viewModel()

        vm.uiState.test {
            skipItems(1) // initial
            advanceUntilIdle()
            val resolved = awaitItem()
            assertThat(resolved.article).isEqualTo(article)
            assertThat(resolved.isBookmarked).isFalse()

            bookmarked.value = true
            advanceUntilIdle()

            val updated = awaitItem()
            assertThat(updated.isBookmarked).isTrue()
            assertThat(updated.article).isEqualTo(article)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleBookmark invokes use case and emits BookmarkAdded when not bookmarked`() = runTest {
        every { getNewsDetail(url) } returns flow { emit(article) }
        every { isArticleBookmarked(url) } returns MutableStateFlow(false)
        coEvery { toggleArticleBookmark(article) } returns Unit

        val vm = viewModel()
        // let the VM resolve the article so toggleBookmark has one to operate on
        vm.uiState.test {
            skipItems(1)
            advanceUntilIdle()
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        vm.toggleBookmark()
        advanceUntilIdle()

        coVerify(exactly = 1) { toggleArticleBookmark(article) }
        assertThat(vm.event.value).isEqualTo(NewsArticleEvent.BookmarkAdded)

        vm.consumeEvent()
        assertThat(vm.event.value).isNull()
    }

    @Test
    fun `toggleBookmark emits BookmarkRemoved when already bookmarked`() = runTest {
        every { getNewsDetail(url) } returns flow { emit(article) }
        every { isArticleBookmarked(url) } returns MutableStateFlow(true)
        coEvery { toggleArticleBookmark(article) } returns Unit

        val vm = viewModel()
        vm.uiState.test {
            skipItems(1)
            advanceUntilIdle()
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        vm.toggleBookmark()
        advanceUntilIdle()

        assertThat(vm.event.value).isEqualTo(NewsArticleEvent.BookmarkRemoved)
    }

    @Test
    fun `toggleBookmark is a no-op before the article resolves`() = runTest {
        every { getNewsDetail(url) } returns flow { /* never emits */ }
        every { isArticleBookmarked(url) } returns MutableStateFlow(false)

        val vm = viewModel()
        vm.toggleBookmark()
        advanceUntilIdle()

        coVerify(exactly = 0) { toggleArticleBookmark(any()) }
        assertThat(vm.event.value).isNull()
    }
}
