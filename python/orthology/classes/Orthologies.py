
class Orthologies:
    """ Takes InParanoid entry and creates an Orthologies object """
    _instance = None
    _s1 = None
    _s2 = None
    _p1 = None  # updated with every entry added

    def __init__(self):
        raise RuntimeError("Call instance() instead")

    @classmethod
    def instance(cls, entry):
        """
        Creates a new dict instance and initialize species and protein vars

        :param entry: line of inParanoid file
        :return: current state of dict
        """

        if entry is None:
            return cls._instance
        if cls._instance is None:
            # initialize species and protein
            cls._s1 = entry[2]
            cls._p1 = entry[4]

            cls._instance = {}
            cls._instance[cls._s1] = None   # eventually replace ID with species 2 entry
        return cls._instance

    @classmethod
    def add_protein(cls, entry):
        """
        After dict is intialized, entries will be added with this function.

        :param entry: line of inParanoid file
        :return: updated dict
        """

        # check if the id of the new entry matches the id of the last one
        if cls._instance[cls._s1] is None:
            cls._s2 = entry[2]

            # reset the id to the new species and protein
            cls._instance[cls._s1] = {cls._s2: [[cls._p1, entry[4], entry[0]]]}

            # reset protein to the new value
            cls._p1 = entry[4]

        else:
            # get the id of the last entry in the list
            last_id = cls._instance[cls._s1][cls._s2][-1].pop()

            # if the current entry is in the orthology, append the current protein
            # to the list of orthologies
            if entry[0] == last_id:
                cls._instance[cls._s1][cls._s2][-1].append(entry[4])
                cls._instance[cls._s1][cls._s2][-1].append(entry[0])

            # otherwise, create a new entry with the current protein and the current id
            else:
                cls._instance[cls._s1][cls._s2].append([entry[4], entry[0]])

        return cls._instance



