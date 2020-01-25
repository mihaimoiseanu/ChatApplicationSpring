package com.coders.chat.service.base

import com.coders.chat.persistence.base.BaseEntity
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.validation.annotation.Validated
import java.io.Serializable
import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * Interface that defines the CRUD contract
 * Uses [Validated] annotation to enable method validation in service layer
 *
 * @param <E> A JPA Entity
 * @param <I> unique identifier
</I></E> */
@Validated
interface CrudService<E : BaseEntity, I : Serializable> {
    companion object {
        val LOGGER = LoggerFactory.getLogger(CrudService::class.java)!!
    }

    /**
     * Reads a entity by it's id
     *
     * @param id
     * @return
     */
    fun read(@NotNull id: I): E? {
        return getEntityRepository().getOne(id)
    }

    /**
     * Combines create and update for an entity
     *
     * @param entity
     * @return
     */
    fun save(@NotNull @Valid entity: E): E {
        return getEntityRepository().save(entity)
        /*todo handle org.springframework.dao.DataIntegrityViolationException*/
    }

    /**
     * Delete an entity by id
     *
     * @param id
     */
    fun delete(@NotNull id: I) {
        LOGGER.debug("Delete entity by id {}", id)
        getEntityRepository().deleteById(id)
    }

    /**
     * Delete an entity
     *
     * @param entity
     */
    fun delete(@NotNull entity: E) {
        LOGGER.debug("Deleting entity {}", entity.id)
        getEntityRepository().delete(entity)
    }

    fun getEntityRepository(): JpaRepository<E, I>
}
