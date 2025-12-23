package tech.aiflowy.ai.entity;

import tech.aiflowy.ai.entity.base.DocumentChunkBase;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.RelationOneToOne;
import com.mybatisflex.annotation.Table;

/**
 * 实体类。
 *
 * @author michael
 * @since 2024-08-23
 */

@Table("tb_document_chunk")
public class DocumentChunk extends DocumentChunkBase {

    @RelationOneToOne(selfField = "documentId",
            targetTable = "tb_document",
            targetField = "id",
            valueField = "title")
    @Column(ignore = true)
    private String title;

    /**
     * 相似度
     */
    @Column(ignore = true)
    private Double similarityScore;

    /**
     * 向量 相似度
     */
    @Column(ignore = true)
    private Double vectorSimilarityScore;

    /**
     * elasticSearch 相似度
     */
    @Column(ignore = true)
    private Double elasticSimilarityScore;

    /**
     * 元数据关键词
     */
    @Column(ignore = true)
    private String[] metadataKeyWords;

    /**
     * 元数据问题
     */
    @Column(ignore = true)
    private String[] metadataQuestions;

    public Double getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(Double similarityScore) {
        this.similarityScore = similarityScore;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getMetadataKeyWords() {
        return metadataKeyWords;
    }

    public void setMetadataKeyWords(String[] metadataKeyWords) {
        this.metadataKeyWords = metadataKeyWords;
    }

    public String[] getMetadataQuestions() {
        return metadataQuestions;
    }

    public void setMetadataQuestions(String[] metadataQuestions) {
        this.metadataQuestions = metadataQuestions;
    }

    public Double getElasticSimilarityScore() {
        return elasticSimilarityScore;
    }

    public void setElasticSimilarityScore(Double elasticSimilarityScore) {
        this.elasticSimilarityScore = elasticSimilarityScore;
    }

    public Double getVectorSimilarityScore() {
        return vectorSimilarityScore;
    }

    public void setVectorSimilarityScore(Double vectorSimilarityScore) {
        this.vectorSimilarityScore = vectorSimilarityScore;
    }
}
