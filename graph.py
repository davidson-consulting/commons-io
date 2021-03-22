import os
import json
import sys

import numpy as np
import matplotlib
import matplotlib.pyplot as plt

CPU_MJ_KEY = 'package|uJ'
DURATION_NS_KEY = 'duration|ns'
DRAM_MJ_KEY = 'dram|uJ'

NB_ITERATION = 3
PATH_FOLDER_RESULTS = 'results/'
PATH_FOLDER_RESULTS_GRAPH = 'graph/'

PREFIX_AMPLIFIED_ENERGY_TEST = 'AmplEnergy'

def is_amplified_version_of(test_name, original_test_name):
    splitted_test_name = test_name.split('_')[0].split(PREFIX_AMPLIFIED_ENERGY_TEST)
    return test_name.split('-')[0].split('.')[-1].startswith(PREFIX_AMPLIFIED_ENERGY_TEST) and \
        original_test_name.startswith(splitted_test_name[0]) and \
        original_test_name.endswith(splitted_test_name[1])

def is_amplified(test_name):
    return test_name.split('-')[0].split('.')[-1].startswith(PREFIX_AMPLIFIED_ENERGY_TEST)

def get_energy_data(data):
    return {
        'energy': data[CPU_MJ_KEY],
        'duration': data[DURATION_NS_KEY],
        'dram': data[DRAM_MJ_KEY],
    } if data[CPU_MJ_KEY] > 0 else {}

def read_json(path_to_json):
    with open(path_to_json) as json_file:
        data = json.load(json_file)
    return data

def to_test_name(filename):
    return filename.split('/')[-1][:-5]

def compute_mediane(data):
    data = sorted(data)
    if len(data) % 2 == 0:
        middle_cursor = int(len(data) / 2)
        return (data[middle_cursor - 1] + data[middle_cursor]) / 2
    else:
        return data[int(len(data)/2)]

def is_warmup(filename):
    return filename.split('-')[1].split('.')[0].startswith('warmup')

def read_all_json(path_folder):
    data = {}
    nb_skipped = 0
    for i in range(0, NB_ITERATION):
        for dirName, subdirList, fileList in os.walk(path_folder + str(i)):
            for file in fileList:
                if is_warmup(file):
                    continue
                current_data = get_energy_data(read_json(dirName + '/' + file))
                if not 'energy' in current_data:
                    nb_skipped = nb_skipped + 1
                    continue
                test_name = to_test_name(file)
                if not test_name in data:
                    data[test_name] = []
                data[test_name].append(current_data['energy'])
    medianes = {}
    nb = {}
    for test in data:
        medianes[test] = compute_mediane(data[test])
        nb[test] = len(data[test])
    print(nb_skipped)
    return medianes, nb

if __name__ == '__main__':

    data, nb = read_all_json(PATH_FOLDER_RESULTS)
    numbers = []
    for test in data:
        if not is_amplified(test):
            baseline = data[test]
            points = []
            points.append(baseline)
            for amplified_test in data:
                if is_amplified_version_of(amplified_test, test):
                    numbers.append(nb[amplified_test])
                    amplified = data[amplified_test]
                    points.append(amplified)
            print(nb[test], numbers)
            plt.plot(points, 'o')
            plt.tight_layout()
            plt.savefig(PATH_FOLDER_RESULTS_GRAPH + test + '.png')
            plt.clf()

                    
