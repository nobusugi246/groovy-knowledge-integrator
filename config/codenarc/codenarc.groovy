ruleset {
  ruleset 'rulesets/basic.xml'
  ruleset 'rulesets/braces.xml'
  ruleset 'rulesets/concurrency.xml'
  ruleset('rulesets/convention.xml') {
    exclude 'NoDef'
    exclude 'InvertedIfElse'
  }
  ruleset 'rulesets/design.xml'
  // ruleset 'rulesets/dry.xml'
  ruleset 'rulesets/enhanced.xml'
  ruleset('rulesets/formatting.xml') {
    exclude 'LineLength'
    exclude 'SpaceAroundMapEntryColon'
  }
  ruleset 'rulesets/exceptions.xml'
  ruleset 'rulesets/generic.xml'
  ruleset 'rulesets/grails.xml'
  ruleset 'rulesets/groovyism.xml'
  ruleset('rulesets/imports.xml') {
    exclude 'MisorderedStaticImports'
  }
  ruleset 'rulesets/jdbc.xml'
  ruleset 'rulesets/junit.xml'
  ruleset 'rulesets/logging.xml'
  ruleset 'rulesets/naming.xml'
  ruleset('rulesets/security.xml') {
    exclude 'JavaIoPackageAccess'
  }
  ruleset 'rulesets/size.xml'
  ruleset('rulesets/unnecessary.xml') {
    exclude 'UnnecessaryGetter'
  }
  ruleset 'rulesets/unused.xml'
}
