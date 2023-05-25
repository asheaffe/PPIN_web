
class Orthology:
    _instance = None

    def __init__(self):
        raise RuntimeError("Call instance() instead")

    @classmethod
    def instance(cls, entry):
        if entry is None:
            return cls._instance
        if cls._instance is None:
            cls._instance = {}
        cls._instance[entry[4]] = entry[3]
        return cls._instance
