package com.lesofn.archforge.infrastructure.dictionary;

import com.lesofn.archforge.user.api.domain.dict.SysDictItem;
import com.lesofn.archforge.user.api.domain.dict.SysDictType;
import com.lesofn.archforge.user.api.port.EnumDictionaryPort;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * {@link EnumDictionaryPort} 的基础设施适配器。
 *
 * <p>
 * 内部委托 {@link EnumDictionaryRegistry}（扫描 {@code @Dictionary} 注解枚举），
 * 并在此完成基础设施字典模型到领域类型（{@link SysDictType} / {@link SysDictItem}）的转换，
 * 使领域模块不必感知基础设施类型。
 */
@Component
@RequiredArgsConstructor
public class EnumDictionaryPortAdapter implements EnumDictionaryPort {

    private final EnumDictionaryRegistry registry;

    @Override
    public Optional<SysDictType> findTypeByCode(String dictCode) {
        return registry.findByCode(dictCode).map(this::toType);
    }

    @Override
    public Optional<SysDictType> findTypeById(Long dictTypeId) {
        return registry.findByTypeId(dictTypeId).map(this::toType);
    }

    @Override
    public Optional<SysDictItem> findItemById(Long dictItemId) {
        return registry.findItemById(dictItemId).map(this::toItem);
    }

    @Override
    public List<SysDictType> findAllTypes() {
        return registry.findAll().stream().map(this::toType).toList();
    }

    @Override
    public List<SysDictItem> findItemsByTypeCode(String dictCode) {
        return registry.findByCode(dictCode)
                .map(d -> d.getItems().stream().map(this::toItem).toList())
                .orElse(List.of());
    }

    @Override
    public List<SysDictItem> findItemsByTypeId(Long dictTypeId) {
        return registry.findByTypeId(dictTypeId)
                .map(d -> d.getItems().stream().map(this::toItem).toList())
                .orElse(List.of());
    }

    @Override
    public boolean isEnumDictCode(String dictCode) {
        return registry.isEnumDictCode(dictCode);
    }

    @Override
    public boolean isEnumDictTypeId(Long dictTypeId) {
        return registry.isEnumDictTypeId(dictTypeId);
    }

    @Override
    public boolean isEnumDictItemId(Long dictItemId) {
        return registry.isEnumDictItemId(dictItemId);
    }

    private SysDictType toType(EnumDictionary dict) {
        SysDictType type = new SysDictType();
        type.setDictTypeId(dict.getDictTypeId());
        type.setDictCode(dict.getDictCode());
        type.setDictName(dict.getDictName());
        type.setDescription(dict.getDescription());
        type.setStatus(dict.getStatus());
        type.setSort(dict.getSort());
        return type;
    }

    private SysDictItem toItem(EnumDictionaryItem item) {
        SysDictItem sys = new SysDictItem();
        sys.setDictItemId(item.getDictItemId());
        sys.setDictTypeId(item.getDictTypeId());
        sys.setItemCode(item.getCode());
        sys.setItemLabel(item.getLabel());
        sys.setSort(item.getSort());
        sys.setStatus(item.getStatus());
        return sys;
    }
}
