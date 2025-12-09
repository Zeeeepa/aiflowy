package tech.aiflowy.ai.tinyflow.repository;

import dev.tinyflow.core.chain.NodeState;
import dev.tinyflow.core.chain.repository.NodeStateField;
import dev.tinyflow.core.chain.repository.NodeStateRepository;
import org.springframework.stereotype.Component;
import tech.aiflowy.common.constant.CacheKey;

import java.util.EnumSet;

@Component
public class NodeStateRepositoryImpl extends BaseRepository implements NodeStateRepository {

    @Override
    public NodeState load(String instanceId, String nodeId) {
        String key = CacheKey.NODE_CACHE_KEY + instanceId + ":" + nodeId;
        NodeState nodeState = getCache(key, NodeState.class);
        if (nodeState == null) {
            nodeState = new NodeState();
            nodeState.setChainInstanceId(instanceId);
            nodeState.setNodeId(nodeId);
            putCache(key, nodeState);
        }
        return nodeState;
    }

    @Override
    public boolean tryUpdate(NodeState newState, EnumSet<NodeStateField> fields, long chainStateVersion) {
        String key = CacheKey.NODE_CACHE_KEY + newState.getChainInstanceId() + ":" + newState.getNodeId();
        putCache(key, newState);
        return true;
    }
}
