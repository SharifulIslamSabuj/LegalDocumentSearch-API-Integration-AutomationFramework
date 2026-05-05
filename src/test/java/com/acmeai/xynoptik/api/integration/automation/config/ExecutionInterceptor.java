package com.acmeai.xynoptik.api.integration.automation.config;

import com.acmeai.xynoptik.api.integration.automation.observability.ObservabilityManager;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Enterprise-grade TestNG Method Interceptor
 * Activates ExecutionController to filter tests based on:
 * - CI mode (smoke + regression only)
 * - Group filters (smoke/regression/integration)
 * - Environment configuration
 *
 * Uses ExecutionController as SINGLE SOURCE OF TRUTH for all execution decisions.
 */
public class ExecutionInterceptor implements IMethodInterceptor {

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        List<IMethodInstance> filtered = new ArrayList<>();

        // Intercept and evaluate each test method
        for (IMethodInstance method : methods) {
            if (shouldExecuteTest(method)) {
                filtered.add(method);
                String testName = getTestName(method);
                ObservabilityManager.logRunning(testName);
            } else {
                String testName = getTestName(method);
                String skipReason = getSkipReason(method);
                ObservabilityManager.logSkipped(testName, skipReason);
            }
        }

        // Log comprehensive execution strategy
        ObservabilityManager.logExecutionStrategy(
                ExecutionController.isCI(),
                ExecutionController.getGroups(),
                ExecutionController.getEnv(),
                methods.size(),
                filtered.size()
        );

        return filtered;
    }

    /**
     * Get human-readable test name
     */
    private String getTestName(IMethodInstance method) {
        return method.getMethod().getTestClass().getRealClass().getSimpleName() +
               "." + method.getMethod().getMethodName();
    }

    /**
     * Determine if a test should be executed using ExecutionController as SINGLE SOURCE OF TRUTH
     * Uses isSmoke(), isRegression(), isIntegration() methods from ExecutionController
     */
    private boolean shouldExecuteTest(IMethodInstance method) {
        String[] testGroups = method.getMethod().getGroups();

        // If test has no groups, execute in all modes
        if (testGroups == null || testGroups.length == 0) {
            return true;
        }

        // Check against active execution mode using ExecutionController
        for (String group : testGroups) {
            if (group.equals("smoke") && ExecutionController.isSmoke()) {
                return true;
            }
            if (group.equals("regression") && ExecutionController.isRegression()) {
                return true;
            }
            if (group.equals("integration") && ExecutionController.isIntegration()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get skip reason - for observability
     */
    private String getSkipReason(IMethodInstance method) {
        String[] testGroups = method.getMethod().getGroups();

        if (testGroups == null || testGroups.length == 0) {
            return "NO GROUPS";
        }

        // Check if it's an integration test skipped due to CI mode
        for (String group : testGroups) {
            if (group.equals("integration") && ExecutionController.isCI() &&
                !ExecutionController.getGroups().contains("integration")) {
                return "CI POLICY";
            }
        }

        return "GROUP FILTER";
    }
}
