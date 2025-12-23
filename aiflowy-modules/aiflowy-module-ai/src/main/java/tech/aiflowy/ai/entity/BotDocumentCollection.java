package tech.aiflowy.ai.entity;

import tech.aiflowy.ai.entity.base.BotDocumentCollectionBase;
import com.mybatisflex.annotation.RelationOneToOne;
import com.mybatisflex.annotation.Table;

/**
 *  实体类。
 *
 * @author michael
 * @since 2024-08-28
 */

@Table("tb_bot_document_collection")
public class BotDocumentCollection extends BotDocumentCollectionBase {

    @RelationOneToOne(selfField = "knowledgeId", targetField = "id")
    private DocumentCollection knowledge;

    public DocumentCollection getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(DocumentCollection knowledge) {
        this.knowledge = knowledge;
    }
}
