# Author: Pontus Laestadius
# Since: 04/05/2017
# Version: 0.1

# Compares the test results of two different files.

import sys
from time import gmtime, strftime


def main():
    try:

        with open("testcase_generated.txt", "r") as f:
            list_generated = f.read().splitlines()
        f.close()

        with open("testcase_expected.txt", "r") as f:
            list_expected = f.read().splitlines()
        f.close()

        print("---- Matching lengths ----")
        print("Expected: {} | Received: {}".
              format(len(list_expected), len(list_generated)))
        # Matches their length before iterating over them.
        if len(list_generated) != len(list_expected):
            failed("invalid number of arguments")
        print("---------- Done ----------")

        print("---- Matching results ----")
        i = 1
        for a in list_expected:
            b = list_generated[i -1]
            if a != b:
                failed("on row {}. Expected: {} | Received {}".format(i, a, b))
            print("{}. {} and {}".format(i, a, b))

            i += 1
        print("---------- Done ----------")

        success()

    # Pass a failed if an except is caught.
    except FileNotFoundError:
        print("Files not found.")
        failed("to find files")


def failed(string):
    result = "Failed {}".format(string)
    print(result)
    write(result)
    sys.exit(1)


def success():
    print("Test passed")
    write("success")
    sys.exit(0)


def write(string):
    f = open("testcase_result.txt", "a")
    f.write(time() + " - " + string + "\n")
    f.close()
    sys.exit(0)


def time():
    return strftime("%Y-%m-%d %H:%M:%S", gmtime())


main()