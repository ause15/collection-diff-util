package io.github.diff;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * CollectionDiffUtil
 *
 * <p>
 * 用于同步「前端全量提交的数据」与「数据库已有数据」之间的差异，
 * 自动计算并执行：
 * <ul>
 *     <li>Insert：新增的数据</li>
 *     <li>Update：已存在但需要更新的数据</li>
 *     <li>Delete：已不存在的数据</li>
 * </ul>
 *
 * <p>
 * 典型使用场景：
 * <ul>
 *     <li>用户-角色 / 用户-标签 等关联表更新</li>
 *     <li>表单全量提交，避免 delete + insert</li>
 *     <li>中后台 One-to-Many / Many-to-Many 同步</li>
 * </ul>
 *
 * <p>
 * 特点：
 * <ul>
 *     <li>✅ 强类型（无反射、无字符串字段）</li>
 *     <li>✅ O(n) 时间复杂度</li>
 *     <li>✅ 支持 record 作为联合业务 Key</li>
 * </ul>
 */
public final class CollectionDiffUtil {

    private CollectionDiffUtil() {
    }

    /**
     * 同步两个集合的差异，并分别执行 insert / update / delete 操作
     *
     * @param sourceSupplier 前端/外部传入的数据集合
     * @param targetSupplier 数据库中已有的数据集合
     * @param sourceKeyExtractor source 的业务唯一 Key 提取器
     * @param targetKeyExtractor target 的业务唯一 Key 提取器
     * @param insertOperation 插入操作
     * @param updateOperation 更新操作（source -> target）
     * @param deleteOperation 删除操作
     * @param <S> Source（DTO / Request）
     * @param <T> Target（Entity）
     * @param <K> 业务唯一 Key（通常是 record）
     */
    public static <S, T, K> void sync(
            Supplier<Collection<S>> sourceSupplier,
            Supplier<Collection<T>> targetSupplier,
            Function<S, K> sourceKeyExtractor,
            Function<T, K> targetKeyExtractor,
            Consumer<Collection<S>> insertOperation,
            Consumer<Map<S, T>> updateOperation,
            Consumer<Collection<T>> deleteOperation
    ) {

        Collection<S> sources = Optional.ofNullable(sourceSupplier)
                .map(Supplier::get)
                .orElse(Collections.emptyList());

        Collection<T> targets = Optional.ofNullable(targetSupplier)
                .map(Supplier::get)
                .orElse(Collections.emptyList());

        // 建立 target 索引：K -> T
        Map<K, T> targetIndex = new HashMap<>(targets.size());
        for (T target : targets) {
            K key = targetKeyExtractor.apply(target);
            targetIndex.put(key, target);
        }

        Collection<S> inserts = new ArrayList<>();
        Map<S, T> updates = new HashMap<>();

        // 遍历 source，判断 insert / update
        for (S source : sources) {
            K key = sourceKeyExtractor.apply(source);
            T target = targetIndex.remove(key);
            if (target == null) {
                inserts.add(source);
            } else {
                updates.put(source, target);
            }
        }

        // 剩余未匹配的 target 即为 delete
        Collection<T> deletes = targetIndex.values();

        if (!inserts.isEmpty()) {
            insertOperation.accept(inserts);
        }
        if (!updates.isEmpty()) {
            updateOperation.accept(updates);
        }
        if (!deletes.isEmpty()) {
            deleteOperation.accept(deletes);
        }
    }
}
