import React, { useEffect, useState } from "react";
import axios from "axios";
import Layout from "../components/Layout";

export default function NotesPage() {
    const [notes, setNotes] = useState([]);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [newNote, setNewNote] = useState({
        title: "",
        contenu: "",
    });
    const [editingNote, setEditingNote] = useState(null);

    useEffect(() => {
        const professeurId = localStorage.getItem("professeurId");
        if (!professeurId) return;

        axios.get(`http://localhost:8080/api/notes/${professeurId}`)
            .then(res => setNotes(res.data))
            .catch(err => console.error(err));
    }, []);

    const handleAddNote = async () => {
        const note = {
            professeurId: localStorage.getItem("professeurId") || "prof123",
            contenu: newNote.contenu,
            title: newNote.title,
        };

        try {
            const response = await fetch("http://localhost:8080/api/notes", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(note),
            });

            if (response.ok) {
                alert("Note ajoutée !");
                setIsModalOpen(false);
                setNewNote({ title: "", contenu: "" });

                const updatedNotes = await fetch(`http://localhost:8080/api/notes/${note.professeurId}`).then(res => res.json());
                setNotes(updatedNotes);
            } else {
                alert("Erreur lors de l'ajout");
            }
        } catch (error) {
            console.error("Erreur réseau :", error);
        }
    };

    const handleEditNote = (note) => {
        setEditingNote(note);
        setNewNote({
            title: note.title,
            contenu: note.contenu,
        });
        setIsModalOpen(true);  // Ouvre le modal en mode édition
    };

    const handleSaveNote = async () => {
        const updatedNote = {
            ...editingNote,
            title: newNote.title,
            contenu: newNote.contenu,
        };

        try {
            const response = await fetch(`http://localhost:8080/api/notes/${editingNote.id}`, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(updatedNote),
            });

            if (response.ok) {
                alert("Note modifiée !");
                const updatedNotes = await fetch(`http://localhost:8080/api/notes/${updatedNote.professeurId}`).then(res => res.json());
                setNotes(updatedNotes);
                setIsModalOpen(false);
                setEditingNote(null);
                setNewNote({ title: "", contenu: "" });
            } else {
                alert("Erreur lors de la modification");
            }
        } catch (error) {
            console.error("Erreur réseau :", error);
        }
    };

    const handleDeleteNote = async (noteId) => {
        if (!window.confirm("Voulez-vous vraiment supprimer cette note ?")) return;

        try {
            const response = await fetch(`http://localhost:8080/api/notes/${noteId}`, {
                method: "DELETE",
            });

            if (response.ok) {
                alert("Note supprimée !");
                setNotes((prevNotes) => prevNotes.filter(note => note.id !== noteId));
            } else {
                alert("Erreur lors de la suppression");
            }
        } catch (error) {
            console.error("Erreur réseau :", error);
        }
    };

    return (
        <Layout>
            <div className="px-8 py-6 space-y-6">
                {/* Header + bouton */}
                <div className="flex justify-between items-center">
                    <div>
                        <h1 className="text-2xl font-semibold text-gray-800">Bloc-notes</h1>
                        <p className="text-sm text-gray-500">Vos notes personnelles</p>
                    </div>
                    <button
                        className="bg-black text-white px-4 py-2 rounded hover:bg-gray-800"
                        onClick={() => setIsModalOpen(true)}
                    >
                        + Ajouter une note
                    </button>
                </div>

                {/* Notes */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {notes.map(note => (
                        <div key={note.id} className="bg-white p-4 rounded-lg shadow border">
                            <h3 className="text-lg font-semibold text-gray-800">{note.title}</h3>
                            <p className="text-sm text-gray-500">{new Date(note.createdAt).toLocaleDateString()}</p>
                            <p className="mt-2 text-gray-700">{note.contenu}</p>
                            <div className="flex justify-between mt-4">
                                <button
                                    className="text-blue-600"
                                    onClick={() => handleEditNote(note)}
                                >
                                    Modifier
                                </button>
                                <button
                                    className="text-red-600"
                                    onClick={() => handleDeleteNote(note.id)}
                                >
                                    Supprimer
                                </button>
                            </div>
                        </div>
                    ))}
                    {notes.length === 0 && (
                        <p className="text-gray-500 col-span-full">Aucune note enregistrée.</p>
                    )}
                </div>

                {/* Modal */}
                {isModalOpen && (
                    <div className="fixed inset-0 bg-gray-500 bg-opacity-75 flex justify-center items-center z-50">
                        <div className="bg-white p-6 rounded-lg shadow-lg w-96">
                            <h3 className="text-xl font-bold mb-4">{editingNote ? "Modifier la note" : "Ajouter une note"}</h3>
                            <div className="mb-4">
                                <input
                                    type="text"
                                    placeholder="Titre"
                                    className="w-full p-2 border-b-2 border-gray-300 focus:outline-none"
                                    value={newNote.title}
                                    onChange={(e) => setNewNote({ ...newNote, title: e.target.value })}
                                />
                            </div>
                            <div className="mb-4">
                                <textarea
                                    placeholder="Contenu"
                                    className="w-full p-2 border-b-2 border-gray-300 focus:outline-none"
                                    value={newNote.contenu}
                                    onChange={(e) => setNewNote({ ...newNote, contenu: e.target.value })}
                                />
                            </div>
                            <div className="flex justify-between">
                                <button
                                    onClick={() => setIsModalOpen(false)}
                                    className="bg-gray-300 text-black px-4 py-2 rounded"
                                >
                                    Annuler
                                </button>
                                <button
                                    onClick={editingNote ? handleSaveNote : handleAddNote}
                                    className="bg-black text-white px-4 py-2 rounded hover:bg-gray-800"
                                >
                                    {editingNote ? "Enregistrer" : "Ajouter"}
                                </button>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </Layout>
    );
}
