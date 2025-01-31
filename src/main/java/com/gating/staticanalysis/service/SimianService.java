package com.gating.staticanalysis.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gating.service.ProcessUtility;
import com.gating.toolconfig.service.SimianConfig;
import com.gating.toolconfig.service.SimianConfigService;
import com.gating.toolconfig.service.ThresholdConfigService;
import com.gating.toolconfig.service.ToolResponse;

@Service
public class SimianService {

  Logger logger = LoggerFactory.getLogger(SimianService.class);

  private static final String SIMIAN_BIN_PATH = "static-code-analyzers/simian/bin;";
  private static final String SIMIAN_REPORT_PATH = "static-code-analyzers/reports/simian_report.txt";

  @Autowired
  ThresholdConfigService thresholdConfigurationService;

  @Autowired
  SimianConfigService simianConfigService;

  @Autowired
  ProcessUtility processUtility;

  public List<String> getCommand(SimianConfig simianParameters) {

    final StringJoiner simianCommand = new StringJoiner(" ");
    simianCommand.add("java");
    simianCommand.add("-jar");
    simianCommand.add("simian-2.5.10.jar");
    simianCommand.add(
        "-threshold=" + simianParameters.getDuplicateLinesThreshold());
    simianCommand.add("-includes=**/*.java");
    simianCommand.add("-excludes=**/*Test.java");
    simianCommand.add("-formatter=plain");
    simianCommand.add(">");
    simianCommand.add("report.txt");

    final List<String> command = new ArrayList<String>();
    command.add("cmd");
    command.add("/c");
    command.add(simianCommand.toString());

    return command;
  }


  public ToolResponse<Integer> run(String srcPath) {

    final SimianConfig simianConfig = simianConfigService.getConfig();
    processUtility.initProcessBuilder(new File(SIMIAN_BIN_PATH).getAbsolutePath());
    final int simianReturnValue =  processUtility.runProcess(getCommand(simianConfig), new File(srcPath));

    if(simianReturnValue == 0) {
      return new ToolResponse<Integer>(0, 0, "Go");
    }
    return new ToolResponse<Integer>(simianReturnValue, 0, "No Go : Code Duplication Present");
  }
}
