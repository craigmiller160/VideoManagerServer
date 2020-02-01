package io.craigmiller160.videomanagerserver.repository.query

import io.craigmiller160.videomanagerserver.dto.VideoSearch
import org.springframework.stereotype.Component
import javax.persistence.Query

@Component
class SearchQueryBuilder {

    // TODO need a simple unit test for the base method
    fun buildEntitySearchQuery(search: VideoSearch): String {
        return StringBuilder()
                .appendln("SELECT vf FROM VideoFile vf")
                .appendln(buildQueryCriteria(search))
                .appendln(buildQueryOrderBy(search))
                .toString()
                .trim()
    }

    // TODO need a simple unit test for the base method
    fun buildEntityCountQuery(search: VideoSearch): String {
        return StringBuilder()
                .appendln("SELECT COUNT(vf) AS video_file_count FROM VideoFile vf")
                .appendln(buildQueryCriteria(search))
                .toString()
                .trim()
    }

    fun addParamsToQuery(search: VideoSearch, query: Query) {
        search.searchText?.let {
            query.setParameter("searchText", "%$it%")
        }
        search.categoryId?.let {
            query.setParameter("categoryId", it)
        }
        search.seriesId?.let {
            query.setParameter("seriesId", it)
        }
        search.starId?.let {
            query.setParameter("starId", it)
        }
    }

    internal fun buildQueryOrderBy(search: VideoSearch): String {
        return "ORDER BY ${search.sortBy.orderByClause} ${search.sortDir}"
    }

    internal fun buildQueryCriteria(search: VideoSearch): String {
        val queryBuilder = StringBuilder()
        search.categoryId?.let {
            queryBuilder.appendln("LEFT JOIN vf.categories ca")
        }
        search.seriesId?.let {
            queryBuilder.appendln("LEFT JOIN vf.series se")
        }
        search.starId?.let {
            queryBuilder.appendln("LEFT JOIN vf.stars st")
        }

        queryBuilder.appendln("WHERE vf.active = true")

        search.searchText?.let {
            queryBuilder.appendln("AND (LOWER(vf.fileName) LIKE LOWER(:searchText)")
                    .appendln("OR LOWER(vf.displayName) LIKE LOWER(:searchText))")
        }
        search.categoryId?.let {
            queryBuilder.appendln("AND ca.categoryId = :categoryId")
        }
        search.seriesId?.let {
            queryBuilder.appendln("AND se.seriesId = :seriesId")
        }
        search.starId?.let {
            queryBuilder.appendln("AND st.starId = :starId")
        }

        return queryBuilder.toString().trim()
    }

}