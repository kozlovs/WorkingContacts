package ru.kozlovss.workingcontacts.data.postsdata.dao

import androidx.paging.PagingSource
import androidx.room.*
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostEntity

@Dao
interface PostDao {

    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun pagingSource(): PagingSource<Int, PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: List<PostEntity>)

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    suspend fun updateContentById(id: Long, content: String)

    suspend fun save(post: PostEntity) =
        if (post.id == 0L) insert(post) else updateContentById(post.id, post.content)

    @Query("""
        UPDATE PostEntity SET
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
    """)
    suspend fun likeById(id: Long) // TODO: необходимо добавлять ид пользвателя в список ид оставивших лайк

    @Query("DELETE FROM PostEntity WHERE id == :id")
    suspend fun removeById(id: Long)

    @Query("DELETE FROM PostEntity")
    suspend fun clear()

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(post: PostEntity)

    @Query("SELECT COUNT(*) != 0 FROM PostEntity WHERE id == :id")
    suspend fun containsPostWithId(id: Long): Boolean
}