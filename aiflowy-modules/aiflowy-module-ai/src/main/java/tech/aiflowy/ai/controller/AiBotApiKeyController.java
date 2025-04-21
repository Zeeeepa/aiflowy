package tech.aiflowy.ai.controller;

import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.ai.entity.AiBotApiKey;
import tech.aiflowy.ai.service.AiBotApiKeyService;
import tech.aiflowy.common.ai.util.UUIDGenerator;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;

import java.time.LocalDateTime;

/**
 *  控制层。
 *
 * @author wangGangQiang
 * @since 2025-04-18
 */
@RestController
@RequestMapping("/api/v1/aiBotApiKey")
public class AiBotApiKeyController extends BaseCurdController<AiBotApiKeyService, AiBotApiKey> {
    public AiBotApiKeyController(AiBotApiKeyService service) {
        super(service);
    }

    /**
     * 添加（保存）数据
     *
     * @return {@code Result.errorCode == 0} 添加成功，否则添加失败
     */
    @PostMapping("/key/save")
    public Result save() {
        String apiKey = UUIDGenerator.generateUUID();
        AiBotApiKey entity = new AiBotApiKey();
        entity.setApiKey(apiKey);
        entity.setCreated(LocalDateTime.now());
        entity.setStatus(1);
        boolean success = service.save(entity);
        onSaveOrUpdateAfter(entity, true);
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(entity.getClass());
        Object[] pkArgs = tableInfo.buildPkSqlArgs(entity);
        return Result.create(success).set("id", pkArgs);
    }
}