package com.revHub.repository;

import com.revHub.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByUsername(String username);

    // Fetch user along with roles to prevent LazyInitializationException
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.userId = :userId")
    Optional<User> findUserWithRolesById(@Param("userId") Long userId);

    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    @Query("SELECT u.userId, u.username FROM User u WHERE u.username IS NOT NULL AND TRIM(u.username) <> '' AND u.username <> 'N/A'")
    List<Object[]> findAllUserIdsAndNames();

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN u.roles r WHERE " +
            "(:userId IS NULL OR :userId = '' OR CAST(u.userId AS string) = :userId) AND " +
            "(:roleId IS NULL OR :roleId = '' OR CAST(r.roleId AS string) = :roleId) AND " +
            "(:activeStatus IS NULL OR :activeStatus = '' OR CAST(u.active AS string) = :activeStatus)")
    Page<User> searchUsers(
            @Param("userId") String userId,
            @Param("roleId") String roleId,
            @Param("activeStatus") String activeStatus,
            Pageable pageable
    );
}
