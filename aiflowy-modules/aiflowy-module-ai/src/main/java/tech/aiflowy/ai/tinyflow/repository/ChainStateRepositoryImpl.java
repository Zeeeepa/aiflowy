package tech.aiflowy.ai.tinyflow.repository;

import dev.tinyflow.core.chain.ChainState;
import dev.tinyflow.core.chain.repository.ChainStateField;
import dev.tinyflow.core.chain.repository.ChainStateRepository;
import org.springframework.stereotype.Component;
import tech.aiflowy.common.constant.CacheKey;

import java.util.EnumSet;

@Component
public class ChainStateRepositoryImpl extends BaseRepository implements ChainStateRepository {

    @Override
    public ChainState load(String instanceId) {
        String key = CacheKey.CHAIN_CACHE_KEY + instanceId;
        ChainState chainState = getCache(key, ChainState.class);
        if (chainState == null) {
            chainState = new ChainState();
            chainState.setInstanceId(instanceId);
            putCache(key, chainState);
        }
        return chainState;
    }

    @Override
    public boolean tryUpdate(ChainState newState, EnumSet<ChainStateField> fields) {
        String key = CacheKey.CHAIN_CACHE_KEY + newState.getInstanceId();
        putCache(key, newState);
        return true;
    }
}
