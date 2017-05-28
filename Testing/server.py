# Author: Pontus Laestadius.
# Since: 2nd of March, 2017.
# Maintained since: 17th of April 2017.

from receiver import Receiver
import traceback
import os


abspath = os.path.abspath("") + "\\"
abspath = abspath.replace("\\", "/")
print(abspath)


def main():
    # Clears the generated file.
    open(abspath + '_testcase/testcase_generated.txt', 'w').close()

    try:
        write_file = [lines for lines in
                      open(abspath + '_testcase/testcase_write.txt', 'r')]
        # If there is a test case written or not.
        if len(write_file) > 2:
            print("Using testcase written in testcase_write.")
            file = open(abspath + '_testcase/testcase_expected.txt', 'w')
            for lines in write_file:
                file.write(lines)
            file.close()

            print("Generating EyeAutomate testcase")
            file = open(abspath + "EyeAutomatic/scripts/generated.txt", "w")
            file.write("Call \"EyeAutomatic/scripts/defaultTest.txt\"\n")
            for lines in write_file:
                if lines == "a045\n":
                    file.write(call("release.txt"))
                elif lines == "a000\n":
                    file.write(call("steerleft.txt"))
                elif lines == "a090\n":
                    file.write(call("steerright.txt"))
                elif lines == "d090\n":
                    file.write(call("release.txt"))
                elif lines == "d110\n":
                    file.write(call("drivebackward.txt"))
                elif lines == "d070\n":
                    file.write(call("driveforward.txt"))

            file.close()

        else:
            print("No testcase found in testcase_write")
    except:
        traceback.print_exc()

    Receiver("localhost", 9005)


def call(string):
    return "Call \"scripts/" + string + "\"\n"

main()
