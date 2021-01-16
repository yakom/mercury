#!/usr/bin/env python

import argparse
import os
import yakom.j_development as j_development

arg_parser = argparse.ArgumentParser(
    description='runs Mercury locally, outside of a Docker container. '
                'requires yakomish Python environment.')

arg_parser.add_argument(
    '--debug',
    help='should the debug socket be opened?',
    action='store_true')

arguments = arg_parser.parse_args()

catalina_options = '-Xmx1G -Duser.timezone=GMT+1 ' \
    '-Dlogback.configurationFile=./assets/logback.xml ' \
    '-Dspring.jndi.ignore=true '

if arguments.debug:
    catalina_options += '-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9002'

os.environ['CATALINA_OPTS'] = catalina_options

j_development.run_Tomcat(j_development.tomcat_9_path)
