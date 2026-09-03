package com.lesofn.archforge.user.internal.service.dict;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.lesofn.archforge.user.api.dao.dict.SysDictItemRepository;
import com.lesofn.archforge.user.api.dao.dict.SysDictTypeRepository;
import com.lesofn.archforge.user.api.domain.dict.SysDictType;
import com.lesofn.archforge.user.api.errors.AdminUserException;
import com.lesofn.archforge.user.api.port.EnumDictionaryPort;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SysDictServiceImplTest {

    @Mock
    private SysDictTypeRepository typeRepository;
    @Mock
    private SysDictItemRepository itemRepository;
    @Mock
    private EnumDictionaryPort enumDictionaryPort;

    @InjectMocks
    private SysDictServiceImpl service;

    @Test
    void shouldFallbackToEnumWhenTypeCodeNotInDb() {
        when(typeRepository.findByDictCode("common.yesOrNo")).thenReturn(Optional.empty());
        SysDictType enumType = new SysDictType();
        enumType.setDictTypeId(-1L);
        enumType.setDictCode("common.yesOrNo");
        enumType.setDictName("是否");
        when(enumDictionaryPort.findTypeByCode("common.yesOrNo")).thenReturn(Optional.of(enumType));

        Optional<SysDictType> result = service.findTypeByCode("common.yesOrNo");

        assertTrue(result.isPresent());
        assertEquals("common.yesOrNo", result.get().getDictCode());
        assertEquals("是否", result.get().getDictName());
    }

    @Test
    void shouldRejectSavingEnumType() {
        when(enumDictionaryPort.isEnumDictCode("common.yesOrNo")).thenReturn(true);

        SysDictType type = new SysDictType();
        type.setDictCode("common.yesOrNo");

        assertThrows(AdminUserException.class, () -> service.saveType(type));
    }
}
