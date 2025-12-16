package tech.aiflowy.ai.tinyflow.service;

import dev.tinyflow.core.chain.ChainState;
import dev.tinyflow.core.chain.ExceptionSummary;
import dev.tinyflow.core.chain.NodeState;
import dev.tinyflow.core.chain.NodeStatus;
import dev.tinyflow.core.chain.repository.ChainStateRepository;
import dev.tinyflow.core.chain.repository.NodeStateRepository;
import dev.tinyflow.core.chain.runtime.ChainExecutor;
import org.springframework.stereotype.Component;
import tech.aiflowy.ai.tinyflow.entity.ChainInfo;
import tech.aiflowy.ai.tinyflow.entity.NodeInfo;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class TinyFlowService {

    @Resource
    private ChainExecutor chainExecutor;

    /**
     * 获取执行状态
     */
    public ChainInfo getChainStatus(String executeId, List<NodeInfo> nodes) {
        ChainStateRepository chainStateRepository = chainExecutor.getChainStateRepository();
        NodeStateRepository nodeStateRepository = chainExecutor.getNodeStateRepository();

        ChainState chainState = chainStateRepository.load(executeId);

        ChainInfo res = getChainInfo(executeId, chainState);

        Set<String> childStateIds = chainState.getChildStateIds();

        for (NodeInfo node : nodes) {
            String nodeId = node.getNodeId();
            NodeState nodeState = nodeStateRepository.load(executeId, nodeId);
            if (NodeStatus.READY.equals(nodeState.getStatus())) {
                if (childStateIds != null && !childStateIds.isEmpty()) {
                    for (String childStateId : childStateIds) {
                        ChainState childChainState = chainStateRepository.load(childStateId);
                        NodeState nodeStateInChild = nodeStateRepository.load(childStateId, nodeId);
                        setNodeStatus(node, nodeStateInChild, childChainState);
                    }
                }
            } else {
                setNodeStatus(node, nodeState, chainState);
            }
            res.getNodes().put(nodeId, node);
        }
        return res;
    }

    private static ChainInfo getChainInfo(String executeId, ChainState chainState) {
        ChainInfo res = new ChainInfo();
        res.setExecuteId(executeId);
        res.setStatus(chainState.getStatus().getValue());
        ExceptionSummary chainError = chainState.getError();
        if (chainError != null) {
            res.setMessage(chainError.getRootCauseClass() + " --> " + chainError.getRootCauseMessage());
        }
        Map<String, Object> executeResult = chainState.getExecuteResult();
        if (executeResult != null && !executeResult.isEmpty()) {
            res.setResult(executeResult);
        }
        return res;
    }

    private void setNodeStatus(NodeInfo node, NodeState nodeState, ChainState chainState) {
        String nodeId = node.getNodeId();
        node.setStatus(nodeState.getStatus().getValue());
        ExceptionSummary error = nodeState.getError();
        if (error != null) {
            node.setMessage(error.getRootCauseClass() + " --> " + error.getRootCauseMessage());
        }
        Map<String, Object> nodeExecuteResult = chainState.getNodeExecuteResult(nodeId);
        if (nodeExecuteResult != null && !nodeExecuteResult.isEmpty()) {
            node.setResult(nodeExecuteResult);
        }
        node.setSuspendForParameters(chainState.getSuspendForParameters());
    }
}
