package com.lzx.starrysky.cache

import android.content.Context
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheSpan
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.lzx.starrysky.SongInfo
import com.lzx.starrysky.utils.StarrySkyConstant
import java.io.File

class ExoCache(private val context: Context,
               private val cacheDir: String?,
               private val cacheMaxBytes: Long
) : ICache {

    private var cacheFile: File? = null
    private var exoCache: Cache? = null

    override fun getProxyUrl(url: String, songInfo: SongInfo): String? {
        //do nothing
        return null
    }

    override fun isOpenCache(): Boolean {
        return StarrySkyConstant.KEY_CACHE_SWITCH
    }

    override fun getCacheDirectory(context: Context, destFileDir: String?): File? {
        if (cacheFile == null && !destFileDir.isNullOrEmpty()) {
            cacheFile = File(destFileDir).apply { this.takeIf { cacheFile?.exists() == false }?.mkdirs() }
        }
        if (cacheFile == null) {
            cacheFile = context.getExternalFilesDir(null).takeIf { it == null } ?: context.filesDir
        }
        return cacheFile
    }

    /**
     * 获取缓存实例
     */
    @Synchronized
    fun getDownloadCache(): Cache? {
        if (exoCache == null) {
            val cacheFile = getCacheDirectory(context, cacheDir) ?: return null
            val cacheEvictor = LeastRecentlyUsedCacheEvictor(cacheMaxBytes)
            exoCache = SimpleCache(cacheFile, cacheEvictor, ExoDatabaseProvider(context))
        }
        return exoCache
    }

    override fun isCache(url: String): Boolean {
        val isCache: Boolean
        val cache = getDownloadCache()
        if (url.isNotEmpty()) {
            val cachedSpans = cache?.getCachedSpans(url)
            if (cachedSpans?.size == 0) {
                isCache = false
            } else {
                isCache = cache?.let {
                    val contentLength = cache.getContentMetadata(url)["exo_len", C.LENGTH_UNSET.toLong()]
                    var currentLength: Long = 0
                    for (cachedSpan in cachedSpans ?: hashSetOf<CacheSpan>()) {
                        currentLength += cache.getCachedLength(url, cachedSpan.position, cachedSpan.length)
                    }
                    return currentLength >= contentLength
                } ?: false
            }
        } else {
            isCache = false
        }
        return isCache
    }
}