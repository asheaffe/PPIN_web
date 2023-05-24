'''
By Brian Law
'''

"""
Read in the provided molecular interactions vocabulary file (i.e. mi.owl, downloaded
from EBI.

:param mi_file: the filepath for the vocabulary file.
:return: a dictionary mapping each MI term to its immediate parent.
"""
def read_mi_tree(mi_file: str) -> dict[str, str]:
  with open(mi_file, encoding='utf-8') as f:
    mi_tree = {}
    test = {}

    '''
    while line := f.readline():
      print(line)

    '''
    new_term = None
    for line in f:
      if line[:3] == 'id:':
        new_term = line[4:].strip()
      elif line[:5] == 'is_a:':
        if new_term not in mi_tree:
          mi_tree[new_term] = set()
        parent = line[6:line.find('!')].strip()
        mi_tree[new_term].add(parent)
        if parent not in test:
          test[parent] = [new_term]
        else:
          test[parent].append(new_term)
      

  #print(test)
  return mi_tree

"""
Find all the descendants of specified MI codes in a tree of MI codes.

:param mi_tree: a dictionary mapping MI codes to their parents
:param codes: the codes for which to find all descendants
:return: all the codes in the MI-tree that are descendants of the specified codes
"""
def get_mi_descendants(mi_tree: dict[str, str], codes: set[str]) -> set[str]:
  size = 0
  
  while size != len(codes):
    remove_me = set()
    size = len(codes)
    for term in mi_tree:
      for parent in mi_tree[term]:
        if parent in codes:
          codes.add(term)
          remove_me.add(term)

    for code in remove_me:
      del mi_tree[code]

  return codes

"""
Get all the MI codes that represent physical molecular interactions.

:param mi_tree: a dictionary mapping MI codes to their parents
:return: all the codes in the MI-tree that are descendants of physical interaction codes
"""
def get_physical_interaction_codes(mi_tree: dict[str, str]):
  physical_codes = {'MI:0407'}
  #print(sorted(get_mi_descendants(mi_tree.copy(), physical_codes)))
  return get_mi_descendants(mi_tree.copy(), physical_codes)

"""
Get all the MI codes that represent experimentally-detected molecular interactions.

:param mi_tree: a dictionary mapping MI codes to their parents
:return: all the codes in the MI-tree that are descendants of experimentally-detected interaction codes
"""
def get_experimental_codes(mi_tree):
  experimental_codes = {'MI:0045'}
  return get_mi_descendants(mi_tree.copy(), experimental_codes)


owl = "test_obo.obo"

physical_interaction_file = open("physical_interaction_codes.txt", 'w')
experimental_detected_file = open("experimental_detected_codes.txt", 'w')

mi_tree = read_mi_tree(owl)

physical_code_set = get_physical_interaction_codes(mi_tree)
experimental_code_set = get_experimental_codes(mi_tree)

for code in physical_code_set:
  physical_interaction_file.write(code + '\n')

for code in experimental_code_set:
  experimental_detected_file.write(code + '\n')
    

physical_interaction_file.close()
experimental_detected_file.close()
