package com.goldcompany.edicon.data.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.goldcompany.edicon.data.network.PixabayApiService
import com.goldcompany.edicon.data.network.model.ImageModel
import java.io.IOException
import retrofit2.HttpException

private const val STARTING_PAGE_INDEX = 1

class ImagePagingSource(
    private val pixabayApiService: PixabayApiService,
    private val query: String
) : PagingSource<Int, ImageModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageModel> {
        val page = params.key ?: STARTING_PAGE_INDEX
        return try {
            val response = pixabayApiService.searchImages(
                query = query,
                page = page,
                perPage = params.loadSize
            )
            val images = response.hits

            val uniqueImages = images.distinctBy { it.id }

            LoadResult.Page(
                data = uniqueImages,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (uniqueImages.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ImageModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}