package com.gating.service;

public class QualityParameters{

  private int timeToRunTests;
  private int noOfWarnings;
  private float codeCoverage;
  private int cyclomaticComplexity;
  private boolean isCodeDuplication;
  private int securityIssuesCount;
  private String comparedToPreviousRun;
  private String finalDecision;


  public int getSecurityIssuesCount() {
    return securityIssuesCount;
  }

  public void setSecurityIssuesCount(int securityIssuesCount) {
    this.securityIssuesCount = securityIssuesCount;
  }

  public boolean isCodeDuplication() {
    return isCodeDuplication;
  }

  public void setCodeDuplication(boolean isCodeDuplication) {
    this.isCodeDuplication = isCodeDuplication;
  }

  public int getTimeToRunTests() {
    return timeToRunTests;
  }

  public void setTimeToRunTests(int timeToRunTests) {
    this.timeToRunTests = timeToRunTests;
  }

  public int getNoOfWarnings() {
    return noOfWarnings;
  }

  public void setNoOfWarnings(int noOfWarnings) {
    this.noOfWarnings = noOfWarnings;
  }

  public float getCodeCoverage() {
    return codeCoverage;
  }

  public void setCodeCoverage(float codeCoverage) {
    this.codeCoverage = codeCoverage;
  }

  public int getCyclomaticComplexity() {
    return cyclomaticComplexity;
  }

  public void setCyclomaticComplexity(int cyclomaticComplexity) {
    this.cyclomaticComplexity = cyclomaticComplexity;
  }

  public String getFinalDecision() {
    return finalDecision;
  }

  public void setFinalDecision(String finalDecision) {
    this.finalDecision = finalDecision;
  }

  public String getComparedToPreviousRun() {
    return comparedToPreviousRun;
  }

  public void setComparedToPreviousRun(String comparedToPreviousRun) {
    this.comparedToPreviousRun = comparedToPreviousRun;
  }


}
