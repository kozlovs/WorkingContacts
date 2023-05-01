package ru.kozlovss.workingcontacts.domain.usecases

import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.dto.MediaModel
import ru.kozlovss.workingcontacts.data.mediadata.dto.Media
import ru.kozlovss.workingcontacts.data.mediadata.repository.MediaRepository
import ru.kozlovss.workingcontacts.data.mywalldata.dao.MyWallDao
import ru.kozlovss.workingcontacts.data.postsdata.dao.PostDao
import ru.kozlovss.workingcontacts.data.postsdata.dto.PostRequest
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostEntity
import ru.kozlovss.workingcontacts.data.postsdata.repository.PostRepository
import ru.kozlovss.workingcontacts.domain.error.mapExceptions
import javax.inject.Inject

class SavePostUseCase @Inject constructor(
    private val postRepository: PostRepository,
    private val mediaRepository: MediaRepository,
    private val postDao: PostDao,
    private val myWallDao: MyWallDao
) {
    suspend fun execute(post: PostRequest, model: MediaModel?) = mapExceptions {
        val media = model?.let { uploadMedia(it) }
        val postRequest = media?.let {
            post.copy(
                attachment = Attachment(
                    media.url,
                    model.type
                )
            )
        } ?: post
        val postResponse = postRepository.save(postRequest)
        val postEntity = PostEntity.fromDto(postResponse)
        postDao.insert(postEntity)
        myWallDao.save(postEntity)
    }

    private suspend fun uploadMedia(mediaModel: MediaModel): Media {
        val media = MultipartBody.Part.createFormData(
            "file",
            mediaModel.file?.name,
            requireNotNull(mediaModel.file?.asRequestBody())
        )
        return mediaRepository.uploadMedia(media)
    }
}