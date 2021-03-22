import os
from shutil import copyfile, SameFileError, copytree, rmtree, move

PATH_FOLDER_RESULTS = 'results/'
PATH_TO_TARGET_JJOULES_REPORTS = 'target/jjoules-reports/'

def run_command(command):
    print(command)
    return os.system(command)

def clean_compile():
    run_command(
        'mvn -Drat.skip=true -Djacoco.skip=true -Danimal.sniffer.skip=true clean test -DskipTests'
    )

def run_test(test_classes):
    run_command(
        'mvn -Drat.skip=true -Djacoco.skip=true -Danimal.sniffer.skip=true test -Dtest=' + 
        ','.join([
            test_classes[0],
            test_classes[1]
        ])
    )

def copy_jjoules_reports_for_iteration(iteration):
    try:
        copytree(PATH_TO_TARGET_JJOULES_REPORTS, PATH_FOLDER_RESULTS + str(iteration) + '/')
    except (SameFileError):
       print('error while copying jjoules reports folder')

def delete_directory(directory):
    if os.path.isdir(directory):
        rmtree(directory)

def mkdir(path):
    try:
        os.makedirs(path)
    except FileExistsError:
        print('pass...')

def copy_and_delete_jjoules_reports_for_iteration(iteration):
    copy_jjoules_reports_for_iteration(iteration)
    delete_directory(PATH_TO_TARGET_JJOULES_REPORTS)

NB_ITERATION = 200
test_classes = [
    'org.apache.commons.io.monitor.AmplEnergyFileAlterationObserverTestCase',
    'org.apache.commons.io.monitor.FileAlterationObserverTestCase'
]

if __name__ == '__main__':

    clean_compile()
    mkdir(PATH_FOLDER_RESULTS)
    
    for i in range(0, NB_ITERATION):
        run_test(test_classes)
        copy_and_delete_jjoules_reports_for_iteration(i)
