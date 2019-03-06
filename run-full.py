#!/usr/bin/env python3
# -*- coding: utf-8 -*-

# A script that runs JMH benchmarks, and saves the results
# And then re-generates the applications to run then with PAPI to get HW
# performance counters.

import subprocess

def process_options(options):
    result = ""
    first = True
    for flag in options:
        if first:
            first = False
        else:
            result += " "
        arg = options[flag]
        result += flag + " " + str(arg)

    return result

def run_jmh():
    """
    Runs a JMH Benchmark run and stores the results in a CSV file
    """
    seeds = 200
    jmh_output_file = "jmh_results.csv"

    jmh_options = {
            '-o' : jmh_output_file,
            '-s' : seeds,
            '-mi': 5,
            '-wi': 3,
            '-mt': 250,
            '-wt': 250,
            }

    jmh_command = ["gradle", "jmh", f"--args={process_options(jmh_options)}"]
    print(jmh_command)

# We run jmh first, the results are in jmh_output_file
    subprocess.check_call(jmh_command)

def run_papi(jmh_output_file):
    """
    Takes benchmark run data, re-generates applications mentioned in the file
    and re-runs the applications while gathering performance counters
    """
    papi_options = {
            '-i' : jmh_output_file
            }

# Once the jmh process has been run, we run PAPI
    papi_command = ["gradle", "run", f"--args='{process_options(papi_options)}'"]

    subprocess.check_call(papi_command)

run_jmh()
run_papi("jmh-results.csv")
