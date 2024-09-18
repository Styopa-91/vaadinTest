package com.project.vaadintest.repo;

import com.project.vaadintest.domain.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepo extends JpaRepository<Employee, Long> {
    @Query("from Employee e where concat(coalesce(e.lastName, ''), ' ', coalesce(e.firstName, ''), ' ', coalesce(e.patronymic, '')) like concat('%', :name, '%') ")
    Page<Employee> findByName(@Param("name") String name, Pageable pageable);

    @Query("from Employee e where concat(coalesce(e.lastName, ''), ' ', coalesce(e.firstName, ''), ' ', coalesce(e.patronymic, '')) like concat('%', :name, '%') ")
    List<Employee> findByNameWithoutPagination(@Param("name") String name);

    Page<Employee> findAll(Pageable pageable);
}
