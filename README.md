# JBrainy

This project is a re-implementation of the paper [Brainy: effective selection of
data structures](https://dl.acm.org/citation.cfm?id=1993509) by Jung et al.

The goal of this tool is to generate data sets for training neural networks,
based on benchmarks of randomly generated applications.

## Structure

- `src/jmh`: Source code for benchmarking using JMH and generate benchmark
  report files.
- `src/main`: Source code for implementing data gathering using hardware performance
  counters (and the PAPI library).
    - `src/main/java/`: Application generation
    - `src/main/kotlin/`: Data gathering with PAPI library.
- `src/test`: Unit tests
- `lib`: Local libraries (java bindings to the PAPI library).

## Tasks

The build tool is gradle, the main tasks are: 

- `gradle jmh`: Generates applications and runs a `jmh`-based benchmark to
  generate a file `jmh-results.csv`.
- `gradle run`: Generates applications and gathers their performance counters
- `gradle test`: Runs the unit tests.

