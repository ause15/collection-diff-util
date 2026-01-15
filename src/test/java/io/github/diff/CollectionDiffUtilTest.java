package io.github.diff;

import io.github.diff.key.UserRole;
import io.github.diff.key.UserRoleDTO;
import io.github.diff.key.UserRoleKey;

import java.util.*;

/**
 * Simple, dependency-free test harness for CollectionDiffUtil.sync.
 * Run with Java 17+ (records support) from the project root:
 *
 * javac --release 17 -encoding UTF-8 -d out ^
 *   src/main/java/io/github/diff/CollectionDiffUtil.java ^
 *   src/main/java/io/github/diff/key/UserRoleKey.java ^
 *   src/main/java/io/github/diff/key/UserRoleDTO.java ^
 *   src/main/java/io/github/diff/key/UserRole.java ^
 *   src/test/java/io/github/diff/CollectionDiffUtilTest.java
 *
 * java -cp out io.github.diff.CollectionDiffUtilTest
 */
public class CollectionDiffUtilTest {

    public static void main(String[] args) {
        List<UserRoleDTO> sources = Arrays.asList(
                new UserRoleDTO(1L, "ADMIN"),
                new UserRoleDTO(2L, "USER")
        );

        List<UserRole> targets = Arrays.asList(
                new UserRole(1L, "ADMIN"), // should be updated (matched)
                new UserRole(1L, "USER")   // should be deleted
        );

        List<UserRoleDTO> inserted = new ArrayList<>();
        Map<UserRoleDTO, UserRole> updated = new HashMap<>();
        List<UserRole> deleted = new ArrayList<>();

        CollectionDiffUtil.sync(
                () -> sources,
                () -> targets,
                UserRoleKey::fromDto,
                UserRoleKey::fromEntity,
                inserted::addAll,
                updated::putAll,
                deleted::addAll
        );

        assertEquals(1, inserted.size(), "insert count");
        assertEquals(1, updated.size(), "update count");
        assertEquals(1, deleted.size(), "delete count");

        assertTrue(
                inserted.stream().anyMatch(dto ->
                        Objects.equals(2L, dto.getUserId()) && "USER".equals(dto.getRoleCode())),
                "insert contains new role"
        );
        assertTrue(
                updated.keySet().stream().anyMatch(dto ->
                        Objects.equals(1L, dto.getUserId()) && "ADMIN".equals(dto.getRoleCode())),
                "update contains matched role"
        );
        assertTrue(
                deleted.stream().anyMatch(entity ->
                        Objects.equals(1L, entity.getUserId()) && "USER".equals(entity.getRoleCode())),
                "delete contains removed role"
        );

        System.out.println("CollectionDiffUtil.sync test passed");
    }

    private static void assertEquals(int expected, int actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label);
        }
    }
}
