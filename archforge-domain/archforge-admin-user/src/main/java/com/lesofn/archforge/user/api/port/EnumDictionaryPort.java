package com.lesofn.archforge.user.api.port;

import com.lesofn.archforge.user.api.domain.dict.SysDictItem;
import com.lesofn.archforge.user.api.domain.dict.SysDictType;
import java.util.List;
import java.util.Optional;

/**
 * 枚举字典端口：领域层需要的"枚举字典"查询与只读校验能力。
 *
 * <p>
 * 枚举字典由 infrastructure 扫描 {@code @Dictionary} 注解的枚举得到，属于外部能力，
 * 因此在此声明端口，由 infrastructure 提供适配器实现并经 Spring 装配，
 * 避免领域模块反向依赖 infrastructure。
 *
 * <p>
 * 端口一律返回领域类型（{@link SysDictType} / {@link SysDictItem}），
 * 基础设施类型到领域类型的转换在适配器中完成。
 */
public interface EnumDictionaryPort {

    /**
     * 按字典编码查找枚举字典。
     *
     * @param dictCode 字典编码
     * @return 对应的字典类型，不存在则为空
     */
    Optional<SysDictType> findTypeByCode(String dictCode);

    /**
     * 按字典类型 ID 查找枚举字典。
     *
     * @param dictTypeId 字典类型 ID
     * @return 对应的字典类型，不存在则为空
     */
    Optional<SysDictType> findTypeById(Long dictTypeId);

    /**
     * 按字典项 ID 查找枚举字典项。
     *
     * @param dictItemId 字典项 ID
     * @return 对应的字典项，不存在则为空
     */
    Optional<SysDictItem> findItemById(Long dictItemId);

    /**
     * 返回全部枚举字典类型。
     *
     * @return 全部字典类型
     */
    List<SysDictType> findAllTypes();

    /**
     * 按字典编码查询枚举字典项。
     *
     * @param dictCode 字典编码
     * @return 字典项列表，不存在则为空列表
     */
    List<SysDictItem> findItemsByTypeCode(String dictCode);

    /**
     * 按字典类型 ID 查询枚举字典项。
     *
     * @param dictTypeId 字典类型 ID
     * @return 字典项列表，不存在则为空列表
     */
    List<SysDictItem> findItemsByTypeId(Long dictTypeId);

    /**
     * 判断字典编码是否属于只读的枚举字典。
     *
     * @param dictCode 字典编码
     * @return 是枚举字典返回 true
     */
    boolean isEnumDictCode(String dictCode);

    /**
     * 判断字典类型 ID 是否属于只读的枚举字典。
     *
     * @param dictTypeId 字典类型 ID
     * @return 是枚举字典返回 true
     */
    boolean isEnumDictTypeId(Long dictTypeId);

    /**
     * 判断字典项 ID 是否属于只读的枚举字典。
     *
     * @param dictItemId 字典项 ID
     * @return 是枚举字典项返回 true
     */
    boolean isEnumDictItemId(Long dictItemId);
}
