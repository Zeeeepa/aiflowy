package tech.aiflowy.datacenter.utils;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.schema.Column;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WhereConditionSecurityChecker {

    // 允许的运算符白名单
    private static final Set<String> ALLOWED_OPERATORS = new HashSet<>(Arrays.asList(
            "=", "!=", "<>", "<", ">", "<=", ">=", "LIKE", "IN", "IS NULL", "IS NOT NULL",
            "NOT IN", "NOT LIKE", "BETWEEN", "AND", "OR"
    ));

    // 最大条件嵌套深度
    private static final int MAX_CONDITION_DEPTH = 3;

    // 最大表达式节点数
    private static final int MAX_EXPRESSION_NODES = 20;

    private int currentDepth = 0;
    private int nodeCount = 0;

    public void checkConditionSafety(Expression expr, Set<String> allowColumns) {
        try {
            // 重置计数器
            currentDepth = 0;
            nodeCount = 0;

            // 开始安全检查
            expr.accept(new ExpressionVisitorAdapter() {
                @Override
                protected void visitBinaryExpression(BinaryExpression expr) {
                    checkNodeCount();
                    checkOperator(expr.getStringExpression());
                    super.visitBinaryExpression(expr);
                }

                @Override
                public void visit(Column column) {
                    checkNodeCount();
                    String colName = column.getColumnName();
                    if (!allowColumns.contains(colName)) {
                        throw new SecurityException("非法查询列: " + colName);
                    }
                }

                @Override
                public void visit(Function function) {
                    throw new SecurityException("where 条件不允许使用函数");
                }

                @Override
                public void visit(AndExpression expr) {
                    enterNestedCondition();
                    super.visit(expr);
                    exitNestedCondition();
                }

                @Override
                public void visit(OrExpression expr) {
                    enterNestedCondition();
                    super.visit(expr);
                    exitNestedCondition();
                }

                @Override
                public void visit(NotExpression expr) {
                    enterNestedCondition();
                    super.visit(expr);
                    exitNestedCondition();
                }

                @Override
                public void visit(LikeExpression expr) {
                    // 检查LIKE模式是否包含通配符攻击
                    String pattern = expr.getRightExpression().toString();
                    if (pattern.matches(".*%[^%]{50,}.*")) {
                        throw new SecurityException("非法通配符");
                    }
                    super.visit(expr);
                }

                @Override
                public void visit(JdbcParameter parameter) {
                    // 允许参数化查询参数
                    checkNodeCount();
                }

                @Override
                public void visit(LongValue value) {
                    checkNodeCount();
                }

                @Override
                public void visit(StringValue value) {
                    checkNodeCount();
                    // 检查字符串值是否包含潜在危险内容
                    String str = value.getValue();
                    if (str.length() > 100) {
                        throw new SecurityException("字符串过长");
                    }
                    if (str.matches(".*[\\x00-\\x1F].*")) {
                        throw new SecurityException("非法字符串");
                    }
                }

                private void checkOperator(String operator) {
                    if (!ALLOWED_OPERATORS.contains(operator.toUpperCase())) {
                        throw new SecurityException("非法操作: " + operator);
                    }
                }

                private void enterNestedCondition() {
                    currentDepth++;
                    if (currentDepth > MAX_CONDITION_DEPTH) {
                        throw new SecurityException("条件嵌套深度过深");
                    }
                }

                private void exitNestedCondition() {
                    currentDepth--;
                }

                private void checkNodeCount() {
                    nodeCount++;
                    if (nodeCount > MAX_EXPRESSION_NODES) {
                        throw new SecurityException("条件表达式节点数过多");
                    }
                }
            });
        } catch (Exception e) {
            throw new SecurityException("条件语句校验失败：", e);
        }
    }
}
