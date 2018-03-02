package com.asemenkov.carpool.logistics.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.asemenkov.carpool.logistics.models.db.Task;

/**
 * @author asemenkov
 * @since Feb 17, 2018
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

	@EntityGraph(value = "taskEntityGraph", type = EntityGraphType.LOAD)
	public List<Task> findByPickupTimeBetween(Date from, Date to);

	@EntityGraph(value = "taskEntityGraph", type = EntityGraphType.LOAD)
	public List<Task> findByIdIn(long... ids);

}
