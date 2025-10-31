package me.matsumo.zencall.core.ui.utils

import androidx.paging.LoadState.NotLoading
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun <T : Any> emptyPaging(): Flow<PagingData<T>> = flowOf(
    PagingData.empty(
        LoadStates(
            prepend = NotLoading(true),
            append = NotLoading(true),
            refresh = NotLoading(true),
        ),
    ),
)

fun <T : Any> LazyPagingItems<T>.isEmpty(): Boolean {
    val isNotLoading = loadState.refresh is NotLoading
    val isReachedEnd = loadState.append.endOfPaginationReached
    val isEmptyItems = itemCount == 0

    return isNotLoading && isReachedEnd && isEmptyItems
}

fun <T : Any> LazyPagingItems<T>?.isNullOrEmpty(): Boolean {
    this ?: return true

    val isNotLoading = loadState.refresh is NotLoading
    val isReachedEnd = loadState.append.endOfPaginationReached
    val isEmptyItems = itemCount == 0

    return isNotLoading && isReachedEnd && isEmptyItems
}

fun <T : Any> createStaticPaging(data: List<T>): Flow<PagingData<T>> {
    val idleLoadState = LoadStates(
        refresh = NotLoading(true),
        prepend = NotLoading(true),
        append = NotLoading(true),
    )

    return flowOf(PagingData.from(data, idleLoadState))
}
