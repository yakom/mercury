#!/usr/bin/env python

import yakom.common as y_common
import yakom.j_development as j_development

j_development.deploy_WAR_as_root(
    j_development.tomcat_9_path,
    y_common.glob_existing('./target/mercury-*.war', throw=True))
