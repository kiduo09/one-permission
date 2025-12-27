package com.zhangyu.permission.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * 数据库初始化服务
 * 在应用启动时检查数据库表和数据，如果不存在则自动创建和初始化
 * 可通过配置 database.auto-init.enabled 控制是否启用（默认：true）
 *
 * @author zhangyu
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseInitializationService implements CommandLineRunner {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    /**
     * 是否启用数据库自动初始化
     * 配置项：database.auto-init.enabled
     * 默认值：true
     */
    @Value("${database.auto-init.enabled:true}")
    private boolean autoInitEnabled;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否启用自动初始化
        if (!autoInitEnabled) {
            log.info("数据库自动初始化已禁用（database.auto-init.enabled=false），跳过初始化");
            return;
        }

        log.info("开始检查数据库初始化状态...");

        try {
            // 检查并初始化数据（包含表创建）
            initializeData();

            log.info("数据库初始化完成");
        } catch (Exception e) {
            log.error("数据库初始化失败", e);
            throw e;
        }
    }


    /**
     * 初始化数据
     */
    private void initializeData() throws IOException {
        log.info("开始初始化数据库表和数据...");

        // 使用统一的初始化脚本（包含表创建和数据初始化）
        initializeTableData("all", "init_database.sql");

        log.info("数据库表和数据初始化完成");
    }


    /**
     * 初始化表和数据
     */
    private void initializeTableData(String tableName, String sqlFile) throws IOException {
        String sql = loadSqlFromFile("sql/" + sqlFile);

        if (StringUtils.hasText(sql)) {
            try {
                // 执行所有SQL语句（包含CREATE TABLE和INSERT语句）
                // 使用更健壮的方式处理SQL语句，避免分号在字符串中的问题
                executeSqlStatements(sql);
                log.info("数据库表和数据初始化成功");
            } catch (Exception e) {
                log.warn("初始化数据库时出错（可能表或数据已存在）: {}", e.getMessage());
            }
        } else {
            log.debug("未找到初始化数据文件: {}", sqlFile);
        }
    }

    /**
     * 执行SQL语句
     */
    private void executeSqlStatements(String sql) {
        // 按行分割，逐行处理
        String[] lines = sql.split("\n");
        StringBuilder statementBuilder = new StringBuilder();
        boolean inStatement = false;

        for (String line : lines) {
            String trimmedLine = line.trim();

            // 跳过注释行和空行
            if (trimmedLine.startsWith("--") || trimmedLine.isEmpty()) {
                continue;
            }

            // 检查是否是CREATE或INSERT语句的开始
            if (!inStatement && (trimmedLine.toUpperCase().startsWith("CREATE") || trimmedLine.toUpperCase().startsWith("INSERT"))) {
                inStatement = true;
                statementBuilder.setLength(0); // 清空构建器
            }

            if (inStatement) {
                statementBuilder.append(line).append("\n");

                // 检查语句是否结束（以分号结尾）
                if (trimmedLine.endsWith(";")) {
                    String statement = statementBuilder.toString().trim();
                    if (!statement.isEmpty()) {
                        try {
                            jdbcTemplate.execute(statement);
                            log.debug("执行SQL语句成功");
                        } catch (Exception e) {
                            log.debug("执行SQL语句失败（可能已存在）: {}", e.getMessage());
                        }
                    }
                    inStatement = false;
                    statementBuilder.setLength(0);
                }
            }
        }
    }

    /**
     * 从文件加载SQL
     */
    private String loadSqlFromFile(String filePath) throws IOException {
        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            if (resource.exists()) {
                return new String(Files.readAllBytes(Paths.get(resource.getURI())));
            }
        } catch (Exception e) {
            log.debug("加载SQL文件 {} 时出错: {}", filePath, e.getMessage());
        }
        return null;
    }
}
