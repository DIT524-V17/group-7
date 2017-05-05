# Author: Pontus Laestadius.
# Since: 2nd of March, 2017.
# Maintained since: 17th of April 2017.

from receiver import Receiver
import traceback

def main():
    # Clears the generated file.
    file = open('testcase_generated.txt', 'w')
    file.close()

    try:
        write_file = [lines for lines in
                      open('C:/Users/pontu/IdeaProjects/group-7/Testing/testcase_write.txt', 'r')]
        # If there is a test case written or not.
        if len(write_file) > 2:
            print("Using testcase written in testcase_write.")
            file = open('testcase_expected.txt', 'w')
            for lines in write_file:
                file.write(lines)
            file.close()

            print("Generating EyeAutomate testcase")
            file = open("C:/EyeAutomate/scripts/generated.txt", "w")
            file.write("Call \"scripts/defaultTest.txt\"\n")
            for lines in write_file:
                if lines == "a045\n":
                    file.write(call("release.txt"))
                elif lines == "a000\n":
                    file.write(call("steerleft.txt"))
                    pass
                elif lines == "a090\n":
                    file.write(call("steerright.txt"))

            file.close()

        else:
            print("No testcase found in testcase_write")
    except:
        traceback.print_exc()

    Receiver("localhost", 9005)


def call(string):
    return "Call \"scripts/" + string + "\"\n"

main()
