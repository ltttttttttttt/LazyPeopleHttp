package com.lt.lazy_people_http.common

import kotlinx.serialization.Serializable

/**
 * creator: lt  2023/3/8  lt.dygzs@qq.com
 * effect :
 * warning:
 */
@Serializable
class Data(
    var `data`: Data? = null,
    var errorCode: Int = 0,
    var errorMsg: String? = null
) {
    @Serializable
    class Data(
        var curPage: Int = 0,
        var datas: List<Data?>? = null,
        var offset: Int = 0,
        var over: Boolean = false,
        var pageCount: Int = 0,
        var size: Int = 0,
        var total: Int = 0
    ) {
        @Serializable
        class Data(
            var adminAdd: Boolean = false,
            var apkLink: String? = null,
            var audit: Int = 0,
            var author: String? = null,
            var canEdit: Boolean = false,
            var chapterId: Int = 0,
            var chapterName: String? = null,
            var collect: Boolean = false,
            var courseId: Int = 0,
            var desc: String? = null,
            var descMd: String? = null,
            var envelopePic: String? = null,
            var fresh: Boolean = false,
            var host: String? = null,
            var id: Int = 0,
            var link: String? = null,
            var niceDate: String? = null,
            var niceShareDate: String? = null,
            var origin: String? = null,
            var prefix: String? = null,
            var projectLink: String? = null,
            var publishTime: Long = 0,
            var realSuperChapterId: Int = 0,
            var route: Boolean = false,
            var selfVisible: Int = 0,
            var shareDate: Long = 0,
            var shareUser: String? = null,
            var superChapterId: Int = 0,
            var superChapterName: String? = null,
            var tags: List<Tag?>? = null,
            var title: String? = null,
            var type: Int = 0,
            var userId: Int = 0,
            var visible: Int = 0,
            var zan: Int = 0
        ) {
            @Serializable
            class Tag(
                var name: String? = null,
                var url: String? = null
            )
        }
    }
}