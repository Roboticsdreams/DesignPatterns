package com.rdreams.suites;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SelectPackages(value = { "com.rdreams.codingchallenges","com.rdreams.interviewprep","com.rdreams.interviewquest"})
@SuiteDisplayName("App Test Suite")
public class AppTestSuite {}